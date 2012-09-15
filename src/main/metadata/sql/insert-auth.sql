INSERT INTO `qv_quiz`.`auth_groups` (`ID`, `GRP_NAME`, `GRP_DESC`) VALUES ('1', 'admin', 'etc. etc ');

--IchBinEinSuperPasswort
INSERT INTO `qv_quiz`.`auth_users` (`ID`, `USER_NAME`, `PASSWORD`) VALUES ('1', 'thierry.peng@businessdecision.com', '9582014fe108c70d5ca6f01ddf5b999ff4a9cea82005a665da05f7787363b77a');


INSERT INTO `qv_quiz`.`auth_user_groups` (`AUTH_USER_ID`, `AUTH_GROUP_ID`) VALUES ('1', '1');