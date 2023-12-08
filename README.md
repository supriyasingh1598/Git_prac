
10 new activities
9 chats with new messages
1 new notification
Chat



has context menu
has context menuYou were mentioned.SMS chat.Unread message.Last message:Group chat.Meeting Chat.Chat.You're starting a new conversation..This chat is muted.You can't send messages because you are not a member of the chat.


Chat

Files
Has context menu

Link https://persistentsystems.sharepoint.c... by Supriya Singh
11/23/22 12:31 PM
Supriya Singh

https://persistentsystems.sharepoint.com/sites/Pi/SitePages/Pi-Home.aspx?OR=Teams-HL&CT=1646304286858&sourceId=&params=%7B%22AppName%22%3A%22Teams-Desktop%22%2C%22AppVersion%22%3A%2227%2F22010300411%22%7D

can you please brief me about yourself and ... by Supriya Singh
11/23/22 5:52 PM
Supriya Singh

can you please brief me about yourself and your project



Do you know about orphan accounts ? How normal accounts become orphan account.



how many year of experience you have in Saviynt ?



On which versions you worked on ?



What difference you found in these versions ?



Have you performed application onboarding. How many ? Can you name some ? What challenges you went through while onboarding ?



What steps you will take to find consider a scenario where user lost access to some entitlement although no attribute present in user update rule which run provisioning rules was updated



can you tell any critical issue you fixed and what steps you followed



how you generate initial password in your project ?



configured any ootb ?



what is reconciliation ?



what is application server and web server




rule designer purpose



have you created connectors from scratch



what are connectors types of connectors



what is event handler? heard about post process event handler



types of users in oim



oim user



what is authentication and authorization



what is oracle webgate



types of reconciliation in oim



diff btwn oim 10g nd oim 11g



what are some benefits of identity management



what is adapter ? adapters available in oim

update arstasks set status=4 where status=1... by Supriya Singh
11/23/22 5:53 PMSupriya Singh

update arstasks set status=4 where status=1 and endpoint in(41,20,22,21)

image by Supriya Singh
11/23/22 5:53 PMSupriya Singh

image

image by Supriya Singh
11/23/22 5:54 PMSupriya Singh

image

Message by Supriya Singh, has an attachment.
11/23/22 5:55 PMSupriya Singh

fn_remove_invalid_email_chars (1).sql
November 24, 2022
for add access task count select accountnam... by Supriya Singh
11/24/22 10:09 AM
Supriya Singh

for add access task count



select accountname,entitlement_valuekey from arstasks where TASKTYPE=1 and status=1 and taskdate like '2022-11-17%'



for remove access tasks count



select count(*) from arstasks where TASKTYPE=2 and status=1 and taskdate like '2022-11-17%'

select count(*) from accounts a join user... by Supriya Singh
11/24/22 10:10 AMSupriya Singh

select count(*)
from accounts a join user_accounts ua on a.accountkey=ua.accountkey
join users u on ua.userkey=u.userkey
join endpoints e on a.endpointkey=e.endpointkey
inner join account_entitlements1 ae1 on a.accountkey=ae1.accountkey
left join entitlement_values ev on ae1.entitlement_valuekey = ev.entitlement_valuekey
where e.endpointname in('SYS_PRD_AD_REBEL') and u.statuskey=1 and a.status IN ('1','Manually Provisioned')
and ev.entitlement_value like '%F3%' u.jobcode='50103804'

update arstasks set status=4 where status=7... by Supriya Singh
11/24/22 10:10 AMSupriya Singh

update arstasks set status=4 where status=7 and ENTITLEMENT_VALUEKEY and endpoint

SELECT distinct       username AS USERNAME,... by Supriya Singh
11/24/22 10:10 AMSupriya Singh

SELECT distinct
     username AS USERNAME,
    a.accountkey AS acctKey,
    a.name as ACCOUNTNAME,
    a.endpointkey as ENDPOINTKEY,
    ep.endpointname as ENDPOINTNAME,
ev.entitlement_valuekey as entvaluekey,
    u.userkey as userKey,
    a.name as accName,
'DeprovisionAccess' AS Default_Action_For_Analytics
FROM
    accounts a,
    users u,
    user_accounts ua,
    endpoints ep,
account_entitlements1 e,
entitlement_values ev
WHERE
    a.accountkey = ua.accountkey
        AND u.userkey = ua.userkey
        AND a.STATUS in('Manually Provisioned',1)
        AND a.ENDPOINTKEY = ep.endpointkey
        AND UPPER(ep.ENDPOINTNAME) = 'SAP_SYS_PRD_PORTAL_NPP'
    AND ev.entitlement_valuekey = 199300
        AND ep.STATUS = 1

November 25, 2022
select u.username,a.name,e.endpointname,ae1... by Supriya Singh
11/25/22 10:21 AM
Supriya Singh

select u.username,a.name,e.endpointname,ae1.entitlement_valuekey as entvaluekey,ae1.accountkey as acctKey, 'Deprovision Access' as 'Default_Action_For_Analytics'  from accounts a join account_entitlements1 ae1 on   a.accountkey = ae1.accountkey join user_accounts ua on a.accountkey=ua.accountkey join users u on u.userkey=ua.userkey join endpoints e on a.endpointkey=e.endpointkey where e.endpointname in ("SAP_SYS_PRD_PORTAL_NPP") and ae1.entitlement_valuekey='199300'




update roles set status=0 where role_name='PrecedaAU';
update roles set status=0 where role_name='PrecedaNZ';

select accountname,USERKEY,ENDPOINT,ENTITLE... by Supriya Singh
11/25/22 10:22 AMSupriya Singh

select accountname,USERKEY,ENDPOINT,ENTITLEMENT_VALUEKEY from arstasks where TASKTYPE=2 and status=3 and taskdate like '2022-11-15%'



select accountname,USERKEY,ENDPOINT,ENTITLEMENT_VALUEKEY from arstasks where TASKTYPE=1 and status=3 and taskdate like '2022-11-15%' and entitlement_valuekey not in(297981,297982,297896,198992,198993,198994)



select accountname,USERKEY,ENDPOINT,ENTITLEMENT_VALUEKEY from arstasks where status=9 and taskdate like '2022-11-15%' and entitlement_valuekey not in(297981,297982,297896,198992,198993,198994)

select count(*) from arstasks where TASKTYP... by Supriya Singh
11/25/22 10:22 AMSupriya Singh

select count(*) from arstasks where TASKTYPE=2 and status=1
select count(*) from arstasks where TASKTYPE=1 and status=1 and taskdate like '2022-11-15%'

"If (EMPLOYEEGROUP IN ""1,2,3,4,5,8"" AND C... by Supriya Singh
11/25/22 10:23 AMSupriya Singh

"If (EMPLOYEEGROUP IN ""1,2,3,4,5,8"" AND COMPANYCODE IN ""1000,2000,3000,4000,4001, 4105, 4010,5000,6510,8110"")
OR (EMPLOYEEGROUP EQUALS ""6"" AND COMPANYCODE  IN ""8000,8001"")
Then
Create Account on SAP Portal Production NPP
AND Create Account on SAP Gateway Production NGP 400
AND Create Account on SAP ECC Production PRD 420
Assign SAP Roles::ZEC.T.SRG.COMMON-ALLUSERS
Assign SAP Roles::ZGW.F.SRG1.COMMON-ACCESS-ALL"




select u.username,u.employeeid,u.firstname,u.lastname,u.companyname,u.jobcode,u.country,u.employeetype from accounts a join user_accounts ua on a.accountkey=ua.accountkey
join users u on ua.userkey=u.userkey
join endpoints e on a.endpointkey=e.endpointkey
inner join account_entitlements1 ae1 on a.accountkey=ae1.accountkey
left join entitlement_values ev on ae1.entitlement_valuekey = ev.entitlement_valuekey
where e.endpointname in('SAP_SYS_PRD_GW_NGP_400') and u.statuskey=1 and a.status IN ('1','Manually Provisioned')
and ev.entitlement_value like '%ZGW.F.SRG1.COMMON-ACCESS-ALL%' and u.employeeid not in(select u.employeeid from accounts a join user_accounts ua on a.accountkey=ua.accountkey
join users u on ua.userkey=u.userkey
join endpoints e on a.endpointkey=e.endpointkey
inner join account_entitlements1 ae1 on a.accountkey=ae1.accountkey
left join entitlement_values ev on ae1.entitlement_valuekey = ev.entitlement_valuekey
where e.endpointname in('SAP_SYS_PRD_GW_NGP_400') and u.statuskey=1 and a.status IN ('1','Manually Provisioned')
and ev.entitlement_value like '%ZGW.F.SRG1.COMMON-ACCESS-ALL%' and ((u.employeetype in(6) and u.companyname in(8000,8001)) OR (u.companyname in(1000,2000,3000,4000,4001, 4105, 4010,5000,6510,8110) and u.employeetype in(1,2,3,4,5,8))))

select us.username,us.employeeid,us.firstna... by Supriya Singh
11/25/22 10:23 AMSupriya Singh

select us.username,us.employeeid,us.firstname,us.lastname,us.companyname,us.jobcode from users us,roles r, role_user_account ra where us.USERKEY = ra.USERKEY and r.ROLEKEY = ra.ROLEKEY and us.statuskey=1 and r.ROLE_NAME='RTM-BCF'  and us.employeeid not in(select us.employeeid from users us, roles r, role_user_account ra where
us.USERKEY = ra.USERKEY
and r.ROLEKEY = ra.ROLEKEY
and us.statuskey=1
and r.ROLE_NAME='RTM-BCF'
and us.jobcode in('50000182','50000190','50168318','50045035','50123588','50179535','50101209','50000136','50059105','50000186','50000191','50118548')
and us.companyname in(4000)
)

SELECT distinct       username AS USERNAME,... by Supriya Singh
11/25/22 10:23 AMSupriya Singh

SELECT distinct
     username AS USERNAME,
    a.accountkey AS acctKey,
    a.name as ACCOUNTNAME,
    a.endpointkey as ENDPOINTKEY,
    ep.endpointname as ENDPOINTNAME,
ev.entitlement_valuekey as entvaluekey,
    u.userkey as userKey,
    a.name as accName,
'DeprovisionAccess' AS Default_Action_For_Analytics
FROM
    accounts a,
    users u,
    user_accounts ua,
    endpoints ep,
account_entitlements1 e,
entitlement_values ev
WHERE
    a.accountkey = ua.accountkey
        AND u.userkey = ua.userkey
        AND a.STATUS in('Manually Provisioned',1)
        AND a.ENDPOINTKEY = ep.endpointkey
        AND UPPER(ep.ENDPOINTNAME) = 'SAP_SYS_PRD_PORTAL_NPP'
    AND ev.entitlement_valuekey = 199301
        AND ep.STATUS = 1
    AND u.username in(226700,226859,232047,232048,212177,212211,215758,219932,232049,232050,232170,215272,226534,227319,228746,232051,232052,232162,232307,212272,215760,216059,226701,227520,232174,232311,218528,226533,228622,229564,232055,232314,212029,212062,212071,224627,224758,225033,212331,214103,215251,225036,225470,225668,226230,227321,231323,231771,232056,212378,218325,218390,224326,225472,225473,212054,212119,215110,215255,216088,218245,218284,218576,224014,224545,227524,231781,216061,216062,218165,218382,218529,231295,232306,212138,213643,216072,218105,225038,225474,231321,231776)

CN=sec_Shared_Mbx_SCA_Store5324,OU=Security... by Supriya Singh
11/25/22 10:24 AMSupriya Singh

CN=sec_Shared_Mbx_SCA_Store5324,OU=Security,OU=Groups,DC=sul,DC=local



If  Users. Job Code IN (50000186,50000191,50118548,50000136,50059105, 50118548) AND COMPANYCODE IN (1000, 2000)
Then
Assign Groups::CN=sec_Shared_Mbx_SCA_Store${user.costcenter.toString()},OU=Security,OU=Groups,DC=sul,DC=local




If  Users. Job Code IN (50000186,50000191,50118548,50000136,50059105, 50118548) AND COMPANYCODE EQ '4000'
Then
Assign Groups::CN=sec_Shared_Mbx_BCF_Store${user.costcenter.toString()},OU=Security,OU=Groups,DC=sul,DC=local



If  Users. Job Code IN (50080800, 50078586, 50146381, 50080775) AND COMPANYCODE = '6510'
Then
Assign Groups::CN=sec_Shared_Mbx_Store${user.costcenter.toString().substring(user.costcenter.length() - 3)},OU=Security,OU=Groups,DC=REBELGROUP,DC=LOCAL

10.112.34.119 SUL-DEV\ADMPERSISTENT pas... by Supriya Singh
11/25/22 10:44 AM
Supriya Singh

10.112.34.119
SUL-DEV\ADMPERSISTENT
pass--C0nnect@5RG

{"connection": "acctAuth", "url": " Link ... by Supriya Singh
11/25/22 10:45 AMSupriya Singh

{"connection": "acctAuth",
"url": "https://intapi.sul.com.au/sap-hr-system-api-srg/api/v1/employees?timeMgmtStatus=1,7,8,9&transactionId=Saviynt&empIdOnly=true",
"httpMethod": "GET",
"httpHeaders":
  {"Authorization": "${access_token}"},
"colsToPropsMap":
  {"customproperty24":"employeeNumber~#~string"}, "userResponsePath": "employees"}

SELECT distinct       username AS USERNAME,... by Supriya Singh
11/25/22 10:49 AMSupriya Singh

SELECT distinct
     username AS USERNAME,
    a.accountkey AS acctKey,
    a.name as ACCOUNTNAME,
    a.endpointkey as ENDPOINTKEY,
    ep.endpointname as ENDPOINTNAME,
    ev.entitlement_valuekey as entvaluekey,
    u.userkey as userKey,
    a.name as accName,
'ProvisionAccess' AS Default_Action_For_Analytics
FROM
    accounts a,
    users u,
    user_accounts ua,
    endpoints ep,
account_entitlements1 e,
entitlement_values ev
WHERE
    a.accountkey = ua.accountkey
        AND u.userkey = ua.userkey
        AND a.STATUS in('Manually Provisioned',1)
        AND a.ENDPOINTKEY = ep.endpointkey
        AND UPPER(ep.ENDPOINTNAME) = 'SAP_SYS_PRD_PORTAL_NPP'
        AND ev.entitlement_valuekey = 199301
        AND ep.STATUS = 1
    AND u.username in(226700,226859,232047,232048,212177,212211,215758,219932,232049,232050,232170,215272,226534,227319,228746,232051,232052,232162,232307,212272,215760,216059,226701,227520,232174,232311,218528,226533,228622,229564,232055,232314,212029,212062,212071,224627,224758,225033,212331,214103,215251,225036,225470,225668,226230,227321,231323,231771,232056,212378,218325,218390,224326,225472,225473,212054,212119,215110,215255,216088,218245,218284,218576,224014,224545,227524,231781,216061,216062,218165,218382,218529,231295,232306,212138,213643,216072,218105,225038,225474,231321,231776)

UPDATE users u SET u.customproperty1 = 'Man... by Supriya Singh
11/25/22 10:50 AMSupriya Singh

UPDATE users u SET u.customproperty1 = 'Manager' WHERE u.employeeid IN(SELECT h.customproperty32 from (SELECT * from users) h where h.customproperty32 is not null);

                      UPDATE users SET cu... by Supriya Singh
11/25/22 10:51 AMSupriya Singh

                     
UPDATE users SET customproperty1 = 'Manager'
   WHERE employeeid IN
      (SELECT REPLACE(LTRIM(REPLACE(customproperty32,'0',' ')),' ','0') as username from users where customproperty32 is not null);
      
      
      UPDATE users u SET u.customproperty1 = 'Manager'
   WHERE u.employeeid IN
      (SELECT h.customproperty32 from users h where h.customproperty32 is not null);

SELECT      username AS USERNAME,      a.ac... by Supriya Singh
11/25/22 10:51 AMSupriya Singh

SELECT
    username AS USERNAME,
    a.accountkey AS acctKey,
    u.STARTDATE as STARTDATE,
    u.CUSTOMPROPERTY39,
    u.email as EMAIL,
    u.firstname as FIRSTNAME,
    u.displayname as DISPLAYNAME,
    u.systemusername as SYSTEMUSERNAME,
    a.name as ACCOUNTNAME,
    a.endpointkey as ENDPOINTKEY,
    ep.endpointname as ENDPOINTNAME,
    'enableAccount' AS Default_Action_For_Analytics
FROM
    accounts a,
    users u,
    user_accounts ua,
    endpoints ep
WHERE
    a.accountkey = ua.accountkey
        AND u.userkey = ua.userkey
        AND a.STATUS in(0,2)
        AND a.ENDPOINTKEY = ep.endpointkey
        AND UPPER(ep.ENDPOINTNAME) = 'SYS_PRD_AD_SUL'
        AND ep.STATUS = 1
        AND u.statuskey =1
           AND date(u.startdate) = curdate()+1

select count(*) from users u join role_user... by Supriya Singh
11/25/22 10:52 AMSupriya Singh

select count(*) from users u join role_user_account rsa on rsa.userkey=u.userkey join roles r on r.rolekey=rsa.rolekey where role_name="People's Manager Role"

update users set password = SHA1('Welcome@1... by Supriya Singh
11/25/22 10:52 AMSupriya Singh

update users set password = SHA1('Welcome@12345'), PASSWORDEXPIRED=1 where username = 'C20477'
