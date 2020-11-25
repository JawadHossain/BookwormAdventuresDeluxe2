#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$FCM_PASSPHRASE" \
--output $HOME/work/BookwormAdventuresDeluxe2/BookwormAdventuresDeluxe2/app/src/main/java/com/example/bookwormadventuresdeluxe2/NotificationUtility/CloudMessagingServerKeyConstant.java \
$HOME/work/BookwormAdventuresDeluxe2/BookwormAdventuresDeluxe2/encrypted_secret/CloudMessagingServerKeyConstant.java.gpg