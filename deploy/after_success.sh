scp target/BridgeMPP-Bot-1.0.0-jar-with-dependencies.jar $SSH_USER@$SSH_HOST:$UPLOAD_PATH
ssh $SSH_USER@$SSH_HOST "echo '$TRAVIS_BUILD_NUMBER' > $UPLOAD_PATH/version.txt"
ssh $SSH_USER@$SSH_HOST "echo '0
!usekey $BRIDGEMPP_KEY
!createalias Build Bot
!subscribegroup $BRIDGEMPP_GROUP
A build has succeeded.
Upload Successful.
Will reload Botwrapper...
?botwrapper reload
' | nc -vw 10 127.0.0.1 $BRIDGEMPP_PORT"