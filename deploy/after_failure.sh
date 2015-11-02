echo "0
!botusekey $BRIDGEMPP_KEY
!botcreatealias Build\ Bot
!botsubscribegroup $BRIDGEMPP_GROUP
A build has failed.
No changes have been propagated to live.
?botwrapper version
" > message.txt
sftp $SSH_USER@$SSH_HOST <<EOF
put message.txt /bots-upload/message.txt
EOF