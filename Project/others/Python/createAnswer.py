import re

file = open('answers','r',encoding='utf-8')
pattern = r'^(\d)+[ 、]*'

indexes=[]
answers=[]
for line in file.readlines():
    index = re.match(pattern,line).group()
    index = re.match(r'^(\d)+',index)
    indexes.append(index.group())
    answers.append(line[re.match(pattern,line).span()[1]:].rstrip().replace('“','\"').replace('”','\"'))
print(indexes)
print(answers)
for i in range(len(indexes)):
    print('INSERT INTO fillquestions VALUES(10,\'填空题\','+indexes[i]+',\''+answers[i]+'\');')
