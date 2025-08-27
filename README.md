# Documentation


## Pre-requite

* Click the following link and follow instructions to [install JBang](https://www.jbang.dev/documentation/jbang/latest/installation.html)
* Install jdk the script uses, e.g:
```
jbang install jdk 24
```

## Use Cases

* Merge 2 PDF document
```
jbang PdfCombiner.java /some/directory/path/OutputFileName.pdf /some/directory/path/source1.pdf /some/directory/path/source2.pdf 
```
* Merge up to 100 PDF files of 10MB or less contained in the same folder
```
jbang MergePdfs.java /some/directory/path/OutputFileName.pdf /some/directory/path/output 
```
* Run from URL, e.g:
```
jbang https://github.com/leandremucyo/scripts/blob/main/MergePdfs.java /some/directory/path/OutputFileName.pdf /some/directory/path/output 
```