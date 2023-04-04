import  re
import os
file = open('questions','r',encoding='utf-8')
pattern1 = r'(\d)+[. ]*、'
filetxt = file.read()
it = re.finditer(pattern1,filetxt)

indexNumbers = [match.span() for match in it]
print(len(indexNumbers))
indexes = []
lines = []
for t in indexNumbers:
    indexes.append(re.match(pattern=r'(\d)+',string=filetxt[t[0]:t[1]]).group())
print(indexes)
file = open('questions','r',encoding='utf-8')


for i in range(len(indexNumbers)-1):
    lines.append(filetxt[indexNumbers[i][1]:indexNumbers[i+1][0]].rstrip())

print(len(indexes),len(lines))
pattern2 = r'(.*)(A)([ 、]+)(.*)'

lines = [re.subn(pattern2,r'\1',line,flags=re.S)[0].rstrip().replace("'","\\'") for line in lines]
i = 0
for line in lines:
    #print('INSERT INTO questions VALUES(11,\'选择题\','+indexes[i]+",\'"+line+'\');')
    i+=1
print(i)
print(len(indexes),len(lines))

sa = []
for i in range(len(indexNumbers)-1):
    sa.append(filetxt[indexNumbers[i][1]:indexNumbers[i+1][0]].rstrip())
patterna = r'(.*)(A)([ 、.]+)(.*)(B)([ 、.]+)(.*)'
sa = [re.subn(patterna,r'\4',line,flags=re.S)[0].rstrip().replace("'","\\'") for line in sa]

sb = []
for i in range(len(indexNumbers)-1):
    sb.append(filetxt[indexNumbers[i][1]:indexNumbers[i+1][0]].rstrip())
patternb = r'(.*)(B)([ 、.]+)(.*)(C)([ 、.]+)(.*)'
sb = [re.subn(patternb,r'\4',line,flags=re.S)[0].rstrip().replace("'","\\'") for line in sb]

sc = []
for i in range(len(indexNumbers)-1):
    sc.append(filetxt[indexNumbers[i][1]:indexNumbers[i+1][0]].rstrip())
patternc = r'(.*)(C)([ 、.]+)(.*)(D)([ 、.]+)(.*)'
sc = [re.subn(patternc,r'\4',line,flags=re.S)[0].rstrip().replace("'","\\'") for line in sc]

sd = []
for i in range(len(indexNumbers)-1):
    sd.append(filetxt[indexNumbers[i][1]:indexNumbers[i+1][0]].rstrip())
patternd = r'(.*)(D)([ 、.]+)(.*)'
sd = [re.subn(patternd,r'\4',line,flags=re.S)[0].rstrip().replace("'","\\'") for line in sd]

answerfile = open('answers','r',encoding='utf-8')
answer = [an for an in answerfile.read() if an in ['A','B',"C","D"]]

for i in range(len(sa)):
     print('INSERT INTO selectquestions VALUES(6,\'选择题\',' + indexes[i] + ",\'" + sa[i] + '\',\''+sb[i]+'\',\''+sc[i]+'\',\''+sd[i]+'\',\''+answer[i]+'\');')



