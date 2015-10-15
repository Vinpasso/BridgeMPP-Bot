echo "The current Branch is $TRAVIS_BRANCH"
echo "The current Pull Request is $TRAVIS_BRANCH"
if [ $TRAVIS_PULL_REQUEST == "false" -a $TRAVIS_BRANCH == "master" ]
then
scp target/BridgeMPP-Bot-1.0.0-jar-with-dependencies.jar $SSH_USER@$SSH_HOST:$UPLOAD_PATH
ssh $SSH_USER@$SSH_HOST "echo '$TRAVIS_BUILD_NUMBER' > $UPLOAD_PATH/version.txt"
ssh $SSH_USER@$SSH_HOST "echo '0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
A build has succeeded.
Upload Successful.
Will reload Botwrapper...
New Version: $TRAVIS_BUILD_NUMBER
?botwrapper reload
' | nc -vw 5 127.0.0.1 $BRIDGEMPP_PORT"
sleep 15
ssh $SSH_USER@$SSH_HOST "echo '0
!usekey $BRIDGEMPP_KEY
!createalias Build Bot
!subscribegroup $BRIDGEMPP_GROUP
New Version has been loaded
New Version: $TRAVIS_BUILD_NUMBER
?botwrapper version
' | nc -vw 5 127.0.0.1 $BRIDGEMPP_PORT"
fi
