#!/bin/bash

function createuser() {
    useradd devops
    mkdir /home/devops/.ssh
    cat > /home/devops/.ssh/authorized_keys << EOF
    ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDJFNc9ld2ZMcvImpXuHjmgYR8PzBpbdcrJDjvRBERCHnqhlGIwdqlhXVRMKdopvHQVPRcrLaCuHJO3d4ODFrD2aTKQXZVSL32TpdDaJUtKRUiFZoxaLtY5WkSTFY+J+vcfKibnvS1sWMH+1H69FAoBXwsIfGfqa/S9eiPpdTRVSJWOzdvLTB6MHBF1A6uS/ZbtVsVEd9LiZgIbbEZqBiM7AeLQpMFQBns953Z+SXuluE+4w5zjAWqwIunnmKaYJTUZKut8el7Dn7Su7u0kmf97CycYuecpLhXpmsPPYd+ZV26GlCIN7i8Uq6itu0Wx1d3W0OZJr2YwgtnT8PyAjO+T root@localhost.localdomain
EOF
    chown -R devops.devops /home/devops
    chmod 700 /home/devops/.ssh
    chmod 600 /home/devops/.ssh/authorized_keys
    echo "devops   ALL=(ALL)       NOPASSWD:ALL" >> /etc/sudoers
    systemctl restart sshd
}