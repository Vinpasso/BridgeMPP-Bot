ssh $SSH_USER@$SSH_HOST "echo '0
!usekey $BRIDGEMPP_KEY
!addalias Build Bot
!joingroup $BRIDGEMPP_GROUP
A build has failed.
No changes have been propagated to live.
?botwrapper version
' | nc -vw 10 127.0.0.1 $BRIDGEMPP_PORT"