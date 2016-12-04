echo "The current Branch is $TRAVIS_BRANCH"
echo "The current Pull Request is $TRAVIS_BRANCH"
if [ $TRAVIS_PULL_REQUEST == "false" -a $TRAVIS_BRANCH == "master" ]
then
sftp $SSH_USER@$SSH_HOST <<EOF
put bridgempp-bots-package/target/bridgempp-bots-package-1.0.0-jar-with-dependencies.jar /bots-upload/BridgeMPP-Bot-1.0.0-jar-with-dependencies.jar
EOF
echo "$TRAVIS_BUILD_NUMBER" > version.txt
sftp $SSH_USER@$SSH_HOST <<EOF
put version.txt /bots-upload/version.txt
EOF
echo "0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
A build has succeeded.
Upload Successful.
Will reload Botwrapper...
New Version: $TRAVIS_BUILD_NUMBER
?botwrapper reload
" > message.txt
sftp $SSH_USER@$SSH_HOST <<EOF
put message.txt /bots-upload/message.txt
EOF
echo "Waiting 300 Seconds for restart"
sleep 300
echo "Wait complete"
echo "0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
New Version has been loaded
New Version: $TRAVIS_BUILD_NUMBER
?botwrapper version
" > message.txt
sftp $SSH_USER@$SSH_HOST <<EOF
put message.txt /bots-upload/message.txt
EOF
fi
