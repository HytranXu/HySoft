#定义编译选项及文件#
#是否使用jjtree-->TRUE FALSE#
USEJT = TRUE
#是否有明确编译，FALSE编译所有java文件#
BUILDDEF = FALSE
#javacc文件 FALSE不处理#
JFILE 	= HyRule_noErro.jj
JFILE2 	= FALSE
JFILE3 	= FALSE
#jjtree文件#
TFILE 	= ./doc/HyRule_noErro.jjt
#git version comments
COMMENT=$(com)	
#git tag comments
TagCOMMENT=$(com)
TagVer=$(ver)	

#以下部分基本不需要调整#
#定义目标#
all: bcp show cleanall runtest move
	
show:
	@ECHO "<---------------Options----------------->"
	@ECHO "Use jjtree --> " $(USEJT)
	@ECHO "Build definitely --> " $(BUILDDEF)
	@ECHO "javacc file --> " $(JFILE)
ifeq ($(USEJT),TRUE)
	@ECHO "jjtree file --> " $(TFILE)
endif
	@ECHO "<-----------------end------------------->"
	@ECHO

#根据USEJT决定编译生成目标#
#生成规则#
GENTARGET=define
#定义删除目标#	
REMOVE=cls	
#编译java目标#
CLASSTOB=cls
#内部临时变量#
CLASSDEF=cls

#使用JJTREE#
ifeq ($(USEJT),TRUE)
	GENTARGET = genjt genjc 
	CLASSDEF = JTclass
	REMOVE = HySoft.class *.class *.java *.jj
#不使用JJTREE#
else
	GENTARGET = genjc 	 
	CLASSDEF = class
	REMOVE = HySoft.class *.class *.java *.jj
endif

#有明确编译#
ifeq ($(BUILDDEF),TRUE)
	CLASSTOB = $(CLASSDEF) 
#无明编译#
else
	CLASSTOB = *.java
	REMOVE = HySoft.class *.class *Constants.java *TokenManager.java  ParseException.java \
TokenMgrError.java Token.java *Parser.java SimpleNode.java SimpleCharStream.java \
*ParserState.java *.java *.jj
endif
   
#填写依赖文件#
JTclass: HySoft.class runtestConstants.class runtestTokenManager.class ParseException.class SimpleCharStream.class Token.class TokenMgrError.class Node.class SimpleNode.class
	
class: HySoft.class runtestConstants.class runtestTokenManager.class ParseException.class Token.class TokenMgrError.class
	
#定义各种依赖项及执行动作# 
runtest: $(GENTARGET) $(CLASSTOB)
	@ECHO "<---------------Build runtest----------------->"
#无明确编译编译所有java#
ifeq ($(BUILDDEF),FALSE)
	javac -Xlint:unchecked $(CLASSTOB)
endif
	@ECHO "<---------------end----------------->"
	@ECHO

#javacc依赖处理#
genjc: $(JFILE)
	@ECHO "<---------------Genjc----------------->"
	./tool/javacc $(JFILE)
ifneq ($(JFILE2),FALSE)
	./tool/javacc $(JFILE2)
endif
ifneq ($(JFILE3),FALSE)
	./tool/javacc $(JFILE3)
endif
	@ECHO "<---------------end----------------->"
	@ECHO

#jjtree依赖处理#
genjt: $(TFILE)
	@ECHO "<---------------Genjt----------------->"
	./tool/jjtree $(TFILE)
	-cp ./doc/code/*.java ./
	-cp ./doc/*.java ./
	@ECHO "<---------------end----------------->"
	@ECHO

#class java依赖处理#
%.class: %.java
	javac $<

move:
	@ECHO "<---------------Move to bin----------------->"
	-mkdir ./bin/HyRules/
	-mkdir ./src/HyRules/
	mv ./*.class ./bin/HyRules/
	mv -f ./*.java ./src/HyRules/
	rm ./*.jj
	@ECHO "<---------------end----------------->"
	@ECHO

#定义清楚动作#
cleanall:
	@ECHO "<---------------CleanAll----------------->"
ifeq ($(USEJT),TRUE)
	rm -rf $(JFILE)
	rm -rf $(REMOVE) ./bin/* out map.log ./*.jj
endif
	rm -f $(REMOVE) ./bin/* out map.log
	@ECHO "<---------------end----------------->"
	@ECHO
	
#定义备份动作#
zip: 
	@ECHO "<---------------zip----------------->"
	-mv ../HyRule_bak6.zip ../HyRule_bak7.zip
	-mv ../HyRule_bak5.zip ../HyRule_bak6.zip
	-mv ../HyRule_bak4.zip ../HyRule_bak5.zip
	-mv ../HyRule_bak3.zip ../HyRule_bak4.zip
	-mv ../HyRule_bak2.zip ../HyRule_bak3.zip
	-mv ../HyRule_bak1.zip ../HyRule_bak2.zip
	-mv ../HyRule_bak.zip ../HyRule_bak1.zip
	-mv ../HyRule.zip ../HyRule_bak.zip
	-rm -rf ./src/* ./bin/*
	-rm -rf ./doc/UC/dump.txt
	-touch ./doc/UC/dump.txt
	-rm -rf ./log.txt
	-rm -rf ./log.txt.bak
	-zip -r ../HyRule.zip ../Hy-Rule/*
	-make
	-make
	-make bak
	
bak:
	@ECHO "<---------------backup----------------->"
	-mkdir ../../bakup
	-mkdir ../../bakup/bak
	-cp ../../bakup/*.zip ../../bakup/bak/
	-cp ../*.zip ../../bakup/
	
clean:
	rm -rf *.class out map.log

test:
	java -classpath ./bin HyRules.HySoft ./doc/UC/vector.txt > ./doc/UC/dump.txt

tshow:
	java -classpath ./bin HyRules.HySoft ./doc/UC/vector.txt > ./doc/参考/tmp.txt
	
bcp:
	-cp ./src/HyRules/AST*.java ./doc/code/
	-cp ./src/HyRules/FuncParameter.java ./doc/code/
	-cp ./src/HyRules/HyRuleDataDef.java ./doc/code/
	-cp ./src/HyRules/HyRuleNode.java ./doc/code/
	-cp ./src/HyRules/LogOrShow.java ./doc/code/
	-cp ./src/HyRules/SimpleNode.java ./doc/code/
	
git:
	@ECHO "<---------------do git----------------->"
ifeq ($(com),)
	@ECHO 'make git com="comment"'
	@ECHO "<---------------end----------------->"
else
	@ECHO Version comment is:  $(COMMENT)
	git status
	git add Makefile
	git add doc
	git add src
	git add .gitignore
	git status
	git commit -m "$(COMMENT)"
	@ECHO "<---------------end----------------->"
endif

push:
	@ECHO "<---------------push git----------------->"
	git push origin dev
	@ECHO "<---------------end----------------->"
	
tag:
	@ECHO "<---------------tag git----------------->"
ifeq ($(com),)
	@ECHO 'make tag com="tagcomment" ver="v1.0"'
	@ECHO "<---------------end----------------->"
else
ifeq ($(ver),)
	@ECHO 'make tag com="tagcomment" ver="v1.0"'
	@ECHO "<---------------end----------------->"
else
	@ECHO tag comment is: $(TagCOMMENT) version is $(TagVer)
	git tag -a $(TagVer) -m "$(TagCOMMENT)"
	git push origin $(TagVer)
	@ECHO "<---------------end----------------->"
endif
endif

status:
	@ECHO "<---------------status git----------------->"
	git status
	@ECHO "<---------------end----------------->"
	
merge:
	@ECHO "<---------------merge to master git----------------->"
	git checkout master
	git merge dev
	git push origin master
	git checkout dev
	git branch
	@ECHO "<---------------end----------------->"
	
log:
	@ECHO "<---------------log git----------------->"
	git log --pretty=format:"%h - %an, %ar : %s"
	@ECHO "<---------------end----------------->"