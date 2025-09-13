JAVADOC = javadoc
JAVAC = javac
JAVA = java

SRC_DIR = src
DOCS_DIR = docs
OUT_DIR = out

PACKAGES = main product
MAIN_CLASS = main.Main

JAVA_SOURCES = $(foreach pkg,$(PACKAGES),$(wildcard $(SRC_DIR)/$(pkg)/*.java))

build:
	$(JAVAC) -d $(OUT_DIR) $(JAVA_SOURCES)

run: build
	$(JAVA) -cp $(OUT_DIR) $(MAIN_CLASS)

docs: build
	$(JAVADOC) -d $(DOCS_DIR) -sourcepath $(SRC_DIR) $(PACKAGES)
