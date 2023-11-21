   pipeline {
    
    agent any
    
    parameters {
        string ( name: 'filename', defaultValue: '', description: '输入需要更新的包名，需要后缀' )
    }
    
    environment {
        filepath="/data/sftp/upload"
        us_host="47.253.136.156"
        ch_host="124.71.36.121"
        us_filepath="/data/www/vhosts/superace.com"
        ch_filepath="/data/www/vhosts/superace.com/dist"
    }

    stages {
        stage('cleanWs build') {
            steps {
                // Clean before build
                cleanWs()
            }
        }
        stage('Check file') {
            steps{
                script{
                    println "---------------------------------------------文件路径为：${filepath}/${filename}----------------------------------------------"                   
                    if( fileExists("${filepath}/${filename}") ){ 
                        echo "文件不存在"
                        
                    }
                    else{
                        echo "文件存在"
                        exit 0
                    fi
                    }
                }
            }
        }
        stage('rsync file') {
            steps{
                script{

                        sh '''
                        /usr/bin/rsync -avzP --bwlimit=500 ${filepath}/${filename} root@${ch_host}:/tmp
                        /usr/bin/rsync -avzP --bwlimit=500 ${filepath}/${filename} root@${us_host}:/tmp
                        '''
   
                  } 
                println "----------------------------------------上传的主机为： ${ch_host} and ${us_host}-----------------------------------------"  
            }
        }
        stage('DEL file') {
          steps{
            script{

                    sh '''
                    ssh root@${ch_host} "unzip -o /tmp/${filename} -d ${ch_filepath}"
                    ssh root@${us_host} "unzip -o /tmp/${filename} -d ${us_filepath}"
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
