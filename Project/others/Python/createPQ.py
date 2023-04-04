questionsFile = open('questions','r',encoding='utf-8')
answersFile = open('answers','r',encoding='utf-8')

chapterName = questionsFile.readlines()
subChapterName = []
for line in answersFile.readlines():
    subs = line.split()
    subChapterName.append(subs)
for i in range(len(chapterName)):
    j  = 1
    for sub in subChapterName[i]:
        print(f"insert into knowledge values({i+1},{j},\'{chapterName[i].strip()}\',\'{sub.strip()}\');")
        j+=1

index1 = []
index2 = []
text1 = questionsFile.read()
for i in range(len(text1)):
    if text1[i].__eq__('@'):
        index1.append(i)

text2 = answersFile.read()
for i in range(len(text2)):
    if text2[i].__eq__('$'):
        index2.append(i)


for i in range(len(index1)-1):
    question = text1[index1[i]+1:index1[i+1]].rstrip().replace("'","\\'")
    print(f'insert into questions values(11,\'编程题\',{i+1},\'{question}\');')


for i in range(len(index1)-1):
    answer =  text2[index2[i]+1:index2[i+1]].rstrip().replace("'","\\'")
    print(f'insert into programquestions values(11,\'编程题\',{i+1},\'{answer}\');')