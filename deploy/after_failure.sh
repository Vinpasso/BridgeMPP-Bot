echo '0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
A build has failed.
No changes have been propagated to live.
?botwrapper version
' > message.txt
scp message.txt $SSH_USER@$SSH_HOST/bots-upload/
