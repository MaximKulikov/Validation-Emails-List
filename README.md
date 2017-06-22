# Validation-Emails-List

Program check every email in list for existence and split into 2 files with good and bad emails.

1. Open config.properties with any text editors and set all variables:
 subsList - file with emails you want to check.
 unsubList - file with emails you want remove from subs list and those dont need to check
 mailFrom - set real email. It been used for validation
 mxDomain - set your mail server domain associate. Usually same as DNS MX record. 
 Should be same as HELO/EHLO request from you mail server.
 
 *If you set incorrect variable, most of mail servers wont answer.  
 *If you don't have mail server or cannot run program from same public IP as you mail server, results wont be good
 
 2. Put all your emails in .txt file you set in subsList, or add into InEmails.txt, if you didnt change parameter
 3. Put all you unsubscribed emails into unsubList .txt file
 
 *don't bother remove extra space at the end of line, program will do it for you
 
 4. There is no simple jar file yet, so you have to compile it somehow
 5. run it from same root path java **ru.maximkulikov.validationemaillist.Validator**
 
 6. watch chaos in console
 
 7. Done. there is 2 file in root directory
##TODO
release .jar  
draw GUI