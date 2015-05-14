openssl aes-256-cbc -k "$KEY" -in deploy/id_rsa.enc -out deploy/id_rsa -d
openssl aes-256-cbc -k "$KEY" -in deploy/id_rsa.pub.enc -out deploy/id_rsa.pub -d
cp deploy/id_rsa ~/.ssh/id_rsa
cp deploy/id_rsa.pub ~/.ssh/id_rsa.pub
chmod 600 ~/.ssh/id_rsa
chmod 600 ~/.ssh/id_rsa.pub
ssh-keyscan -H $SSH_HOST >> ~/.ssh/known_hosts
ssh $SSH_USER@$SSH_HOST "echo '!usekey $BRIDGEMPP_KEY
!addalias Build Bot
!joingroup $BRIDGEMPP_GROUP
A build has been triggered and is now commencing' | nc -vw 10 127.0.0.1 $BRIDGEMPP_PORT"