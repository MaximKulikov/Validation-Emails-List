# Validation-Emails-List

Program check every email in list for existence and split into 2 files with good and bad emails.

##How to?

1. Download .jar

https://github.com/Trinion/Validation-Emails-List/releases/tag/1.0 

For Oracle JAVA SE:

1. run jar, set field

*If you set incorrect variable, most of mail servers wont answer.  
 *If you don't have mail server or cannot run program from same public IP as you mail server, results wont be good
 
2. Start

You need Oracle Java SE 1.8+ for gui, otherwise preconfig `config.properties` and run it from console to see some output.

3. It done when 2 buttons with Good and Bad emails arrive. results store in folder with .jar, make suse it have acces to write
 
 
 
 For Open JDK
 
 1. set config.propersies.
 2. run jar
 3. wait it finish and make 2 result files.
 
 
 Some outdated night text below..
 
 P.S. never edit readme after 2AM.



1.a. for Open JDK, skip for Oracle Java SE
 Open config.properties with any text editors and set all variables:
 subsList - file with emails you want to check.
 unsubList - file with emails you want remove from subs list and those dont need to check
 mailFrom - set real email. It been used for validation
 mxDomain - set your mail server domain associate. Usually same as DNS MX record. 
 Should be same as HELO/EHLO request from you mail server.
 
 
 
 
 2. Put all your emails in .txt or .csv file you set in subsList, or add into InEmails.txt, if you didnt change parameter
 3. Put all you unsubscribed emails into unsubList .txt file
 
 *don't bother remove extra space at the end of line, program will do it for you
 
 
 5. run .jar file, if 
 
 6. watch chaos in console
 
 7. Done. there is 2 file in root directory
