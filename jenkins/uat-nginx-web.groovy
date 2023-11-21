   pipeline {
    
    agent any
    
    parameters {
        choice ( name: 'project', choices: ['uat-admin.updf.com','uat-checkout.updf.com','uat-accounts.updf.com'], description: '选择项目别名' )
        string ( name: 'BRANCH', defaultValue: 'uat', description: '输入需要更新的Git分支' )
    }
    
    environment {
        email_addr="tom@updf.cn"
        harbor="registry.us-east-1.aliyuncs.com"
        GIT_URL_A="git@gitee.com:superace-updf/admin-front.updf.com.git"
        GIT_URL_B="git@gitee.com:superace-updf/checkout.updf.com.git"
        GIT_URL_C="git@gitee.com:superace-updf/accounts.updf.com.git"
        es_host="47.253.33.11" 
        proeject_env="uat"
    }

    stages {
        stage('清除空间') {
            steps {
                // Clean before build
                cleanWs()
            }
        }
        stage('拉取代码') {
            steps {
                script {
                    println "----------------------------------------分支为：${BRANCH}-----------------------------------------"
                    if ( "$project" == "uat-admin.updf.com"  ){
                        try {                                                  
                            checkout(
                                
                                [$class: 'GitSCM', doGenerateSubmoduleConfigurations: false, submoduleCfg: [], extensions: [[$class: 'CloneOption', depth: 1, noTags: false, reference: '', shallow: true]],
                                branches: [[name: "$BRANCH"]],userRemoteConfigs: [[url: "${env.GIT_URL_A}", credentialsId: "20230717"]]]
                            
                            )
                        }catch(exc) {   
                            env.REASON = "拉取代码出错"
                            throw(exc)
                            }
                      }
                    else if ( "$project" == "uat-checkout.updf.com"  ){
                        try {                           
                            checkout( 
                                [$class: 'GitSCM', doGenerateSubmoduleConfigurations: false, submoduleCfg: [], extensions: [[$class: 'CloneOption', depth: 1, noTags: false, reference: '', shallow: true]],
                                branches: [[name: "$BRANCH"]],userRemoteConfigs: [[url: "${env.GIT_URL_B}", credentialsId: "20230717"]]]
                            
                            )
                        }catch(exc) {   
                            env.REASON = "拉取代码出错"
                            throw(exc)
                        }
                    }
                    else {
                        try {                           
                            checkout( 
                                [$class: 'GitSCM', doGenerateSubmoduleConfigurations: false, submoduleCfg: [], extensions: [[$class: 'CloneOption', depth: 1, noTags: false, reference: '', shallow: true]],
                                branches: [[name: "$BRANCH"]],userRemoteConfigs: [[url: "${env.GIT_URL_C}", credentialsId: "20230717"]]]
                            
                            )
                        }catch(exc) {   
                            env.REASON = "拉取代码出错"
                            throw(exc)
                        }
                    }
                    // 定义全局变量
                    env.COMMIT_ID   = sh(script: 'git log --pretty=format:%h',  returnStdout: true).trim() // 提交ID
                    env.COMMIT_USER = sh(script: 'git log --pretty=format:%an', returnStdout: true).trim() // 提交者
                    env.COMMIT_TIME = sh(script: 'git log --pretty=format:%ai', returnStdout: true).trim() // 提交时间
                    env.COMMIT_INFO = sh(script: 'git log --pretty=format:%s',  returnStdout: true).trim() // 提交信息
                    
                    println "------------------------------------------提交ID为： ${COMMIT_ID}--------------------------------------------------- "
                    println "------------------------------------------提交者为： ${env.COMMIT_USER} ---------------------------------------------"
                    println "------------------------------------------提交时间为： ${env.COMMIT_TIME}-------------------------------------------- "
                    println "------------------------------------------提交信息为： ${env.COMMIT_INFO} --------------------------------------------"
                }
            }
        }
        stage('Build Yarn') {
            steps{
                script{
                    println "---------------------------------------------当前工作空间为：${WORKSPACE}----------------------------------------------"                   
                    sh ''' 
                        cd ${WORKSPACE}
                        if [ -d ./dist ];then
                            rm -rf ./dist
                        else
                            echo "dist/ 不存在"
                        fi
                        source /etc/profile
                        yarn
                        yarn ${proeject_env}
                        tar -zcf ${project}.tar.gz dist/
                        rm -f /opt/${proeject_env}-web/${project}.tar.gz && mv ${project}.tar.gz /opt/${proeject_env}-web/
                        
                    '''
               
                }
            }
        }
        stage('Build Image') {
            steps{
                script{

                        sh '''
                        cd  /opt/${proeject_env}-web/ 
                        docker build /opt/${proeject_env}-web/ -t $harbor/updf/${proeject_env}-nginx-web:${COMMIT_ID}
                        docker push $harbor/updf/${proeject_env}-nginx-web:${COMMIT_ID}
                        '''
   
                  } 
                println "----------------------------------------上传镜像为：$harbor/updf/${proeject_env}-nginx-web:${COMMIT_ID}-----------------------------------------"  
            }
        }
        stage('DEL ASK') {
          steps{    
            script{

                    sh '''
                    ssh ecs-user@${es_host} sudo kubectl -n ${proeject_env} set image deployment/${proeject_env}-nginx-web ${proeject_env}-nginx-web=$harbor/updf/${proeject_env}-nginx-web:${COMMIT_ID}
                    '''
                
            }
          }
        }       

    }
    post {
        success {
            feiShuTalk (
                robot: "feishu-failse",
                type: "INTERACTIVE",
                title: "📢 Jenkins 构建通知",
                text: [
                    "📋 **任务名称**：[${JOB_NAME}](${JOB_URL})",
                    "🔢 **任务编号**：[${BUILD_DISPLAY_NAME}](${BUILD_URL})",
                    "🌟 **构建状态**: <font color='green'>成功</font>",
                    "🕐 **构建用时**: ${currentBuild.duration} ms",
                    "👤 **Project Nmae**: ${project}",
                    "<at id=all></at>"
                ],
                buttons: [
                    [
                        title: "更改记录",
                        url: "${BUILD_URL}changes"
                    ],
                    [
                        title: "控制台",
                        type: "danger",
                        url: "${BUILD_URL}console"
                    ]
                ]
            )
        }
        failure {
            feiShuTalk (
                robot: "feishu-failse",
                type: "INTERACTIVE",
                title: "📢 Jenkins 构建通知",
                text: [
                    "📋 **任务名称**：[${JOB_NAME}](${JOB_URL})",
                    "🔢 **任务编号**：[${BUILD_DISPLAY_NAME}](${BUILD_URL})",
                    "🌟 **构建状态**: <font color='red'>失败</font>",
                    "🕐 **构建用时**: ${currentBuild.duration} ms",
                    "👤 **Project Nmae**: ${project}",
                    "<at id=all></at>"
                ],
                buttons: [
                   [
                      title: "更改记录",
                      url: "${BUILD_URL}changes"
                   ],
                   [
                      title: "控制台",
                      type: "danger",
                      url: "${BUILD_URL}console"
                   ]
                ]
              )
        }
    }
 }
