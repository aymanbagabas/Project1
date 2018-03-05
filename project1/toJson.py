#!/usr/bin/python
import json
import glob
import os
import re
import html
import mysql.connector

out = {}

chapterNo=''
chapter=""
section=''
questionNo=1
db = mysql.connector.connect(user='scott',password='tiger',database='javabook')
query = ("INSERT INTO intro11equiz "
         "(chapterNo, questionNo, question, choiceA, choiceB, choiceC, choiceD, choiceE, answerKey, hint) "
         "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)")
cur = db.cursor()

def readChapter(line):
    chapterPattern = re.search('^Chapter', line)
    if chapterPattern:
        m = re.match('^Chapter (\d+) (.+)$', line)
        global chapterNo
        chapterNo = str(m.group(1))
        global chapter
        chapter = 'chapter'+chapterNo
        # out[chapter] = {'name': m.group(2)}
        out[chapter] = {'chapter':chapterNo, 'name': m.group(2), 'questions': []}

# def readSection(line):
#     sectionPattern = re.search('^Section.*$', line)
#     if sectionPattern:
#         m = re.match('^Sections? (\d+\.\d+) (.*)$', line)
#         global section
#         section = 'section'+m.group(1)
#         out[chapter][section] = {'name': m.group(2), 'questions': []}

def readQuestion(questionText):
    text=''
    answers = []
    key = ''
    hint = ''
    for l in questionText.split('\n'):
        line = html.unescape(l)
        if not 'Section' in line:
            #print(line)
            qText = re.match('^\d+\.\s*(.*)$', line)
            aPattern = re.match('^[a-e]\.\s*(.*)$', line)
            keyHintPattern = re.match('^[kK]ey:([a-e]+)\s*(.*)$', line)
            if qText:
                text = qText.group(1)
            elif aPattern:
                answers.append(aPattern.group(1))
            elif keyHintPattern:
                key = keyHintPattern.group(1)
                hint = keyHintPattern.group(2) or ''
            else:
                text = text + '\n' + line
                continue
        # else:
        #     readSection(line)
    global questionNo
    global chapter
    global section
    #print(questionNo, text, answers)
    # out[chapter][section]['questions'].append({'number': questionNo, 'text': text, 'answers': answers, 'keys': key, 'hint': hint})
    out[chapter]['questions'].append({'question': questionNo, 'text': text, 'answers': answers, 'keys': key, 'hint': hint})
    insertToDb(text,answers,key,hint)
    questionNo = questionNo + 1

def insertToDb(t,ans,k,h):
    global db
    global query
    global cur
    a = b = c = d = e = 'NULL'
    for i in range(len(ans)):
        if i == 0:
            a = ans[i]
        elif i == 1:
            b = ans[i]
        elif i == 2:
            c = ans[i]
        elif i == 3:
            d = ans[i]
        elif i == 4:
            e = ans[i]
    if h == '' or h == None:
        h = 'NULL'
    key = ''.join(str(i) for i in k)
    data = (chapterNo, questionNo, t, a, b, c, d, e, key, h)
    cur.execute(query, data)
    db.commit()
    #print(data)

for f in glob.glob("*.txt"):
    questionNo = 1
    fil = open(f, 'r', encoding="ISO-8859-1")
    readChapter(fil.readline())
    #fil.readline()  # empty line
    questions = fil.read().split('\n#')
    for q in questions:
        readQuestion(q)

    # filout = open('../javaprojectjson/'+chapter+'.json', 'w')
    # filout.write(json.dumps(out[chapter]))
    # filout.close()
    fil.close()
cur.close()
db.close()
