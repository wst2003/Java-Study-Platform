import  re
import os
file = open('questions','r',encoding='utf-8')
pattern1 = r'(\d)+[. ]*、'
filetxt = file.read()
it = re.finditer(pattern1,filetxt)

indexNumbers = [match.span() for match in it]
indexes = []
lines = []
for t in indexNumbers:
    indexes.append(re.match(pattern=r'(\d)+',string=filetxt[t[0]:t[1]]).group())
for i in range(len(indexNumbers)-1):
    lines.append(filetxt[indexNumbers[i][1]:indexNumbers[i+1][0]].replace('\n','').rstrip())

i = 0
for line in lines:
    print('INSERT INTO questions VALUES(9,\'判断题\','+indexes[i]+",\'"+line+'\');')
    i+=1
