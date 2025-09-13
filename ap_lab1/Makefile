JAVADOC = javadoc
JAVAC = javac
JAVA = java

SRC_DIR = src
DOCS_DIR = docs
OUT_DIR = out

PACKAGE_NAME = negfib
MAIN_FILE = $(PACKAGE_NAME)/Main

build:
	$(JAVAC) -d $(OUT_DIR) $(SRC_DIR)/$(PACKAGE_NAME)/*.java

run: build
	$(JAVA) -cp $(OUT_DIR) $(MAIN_FILE) $(NUMBER)

docs: build
	$(JAVADOC) -d $(DOCS_DIR) -sourcepath $(SRC_DIR) $(PACKAGE_NAME)
