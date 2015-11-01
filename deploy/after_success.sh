echo "The current Branch is $TRAVIS_BRANCH"
echo "The current Pull Request is $TRAVIS_BRANCH"
if [ $TRAVIS_PULL_REQUEST == "false" -a $TRAVIS_BRANCH == "master" ]
then
scp target/BridgeMPP-Bot-1.0.0-jar-with-dependencies.jar $SSH_USER@$SSH_HOST:$UPLOAD_PATH
echo '$TRAVIS_BUILD_NUMBER' > version.txt
scp version.txt $SSH_USER@$SSH_HOST/bots-upload/
echo '0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
A build has succeeded.
Upload Successful.
Will reload Botwrapper...
New Version: $TRAVIS_BUILD_NUMBER
?botwrapper reload
' > message.txt
scp message.txt $SSH_USER@$SSH_HOST/bots-upload/
sleep 60
echo '0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build Bot
!botsubscribegroup $BRIDGEMPP_GROUP
New Version has been loaded
New Version: $TRAVIS_BUILD_NUMBER
?botwrapper version
' > message.txt
scp message.txt $SSH_USER@$SSH_HOST/bots-upload/
fi
