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
pattern2 = r'[ ]'
lines = [re.sub(pattern2,'_',line) for line in lines]
lines = [re.subn(r'([^_])(_)([^_])',r'\1 \3',line)[0] for line in lines]
lines = [re.subn(r'([^_])(_)([^_])',r'\1 \3',line)[0] for line in lines]

i = 0
for line in lines:
    #print('INSERT INTO questions VALUES(11,\'填空题\','+indexes[i]+",\'"+line+'\');')
    i+=1


print(indexes)
path = 'C:\\Users\\Administrator\\Pictures\\chapterfiles\\'
for i in range(1,12):
    sub = 0
    for subpath in os.listdir(path+f'{i}'):
        sub+=1
        for filename in os.listdir(path+f'{i}\\'+subpath):
            print(f'INSERT chapterfiles VALUES({i},{sub},\'chapterfiles\\\\{i}\\\\{subpath}\\\\{filename}\');')