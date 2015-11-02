openssl aes-256-cbc -k "$KEY" -in deploy/id_rsa.enc -out deploy/id_rsa -d
openssl aes-256-cbc -k "$KEY" -in deploy/id_rsa.pub.enc -out deploy/id_rsa.pub -d
cp deploy/id_rsa ~/.ssh/id_rsa
cp deploy/id_rsa.pub ~/.ssh/id_rsa.pub
chmod 600 ~/.ssh/id_rsa
chmod 600 ~/.ssh/id_rsa.pub
ssh-keyscan -H $SSH_HOST >> ~/.ssh/known_hosts
export COMMIT_MESSAGE="$(git log -n 1 --stat | tr -d \'\"\$ )"
echo $COMMIT_MESSAGE
echo "0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
A build has been triggered and is now commencing with build number: #$TRAVIS_BUILD_NUMBER
This Repository on GitHub: https://github.com/Vinpasso/BridgeMPP-Bot
This Repository on Travis: https://travis-ci.org/Vinpasso/BridgeMPP-Bot
$COMMIT_MESSAGE
" > message.txt
sftp $SSH_USER@$SSH_HOST <<EOF
put message.txt /bots-upload/message.txt
EOF