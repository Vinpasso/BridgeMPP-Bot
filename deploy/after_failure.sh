ssh $SSH_USER@$SSH_HOST "echo '0
!usekey $BRIDGEMPP_KEY
!createalias Build Bot
!subscribegroup $BRIDGEMPP_GROUP
A build has failed.
No changes have been propagated to live.
?botwrapper version
' | nc -vw 5 127.0.0.1 $BRIDGEMPP_PORT"