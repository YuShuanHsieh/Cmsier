# Cmsier
### A content management system for Non-CS users.
- Developer: Yu-Shuan
- Programming language: Java
- GUI library: JavaFx
- IDE: Eclipes
- Additional libraries:
  - [Apache Commons Net](https://commons.apache.org/proper/commons-net/)
  - [Apache Commons IO](https://commons.apache.org/proper/commons-io/)
- Inspiration: Static site generator([Jekyll](https://jekyllrb.com/))
# System Architecture
![System Architecture](http://cherrieblog.tw/project/system_small.png)

### Description
There are five important modules in this project: layout module, setting module, edit module, generator module, and upload module. User can change essential elements of a Web site such as title or style by using a simple interface. After revising page contents and submit them, modules will create individual files which represents different parts of Web page. The responsibility of generate module is to combine these files to form complete Web pages. The last step is uploading final Web pages to user’s Web server. Overall, the ideal method is lets Non-CS users can create their own Web site through several easy steps without any technical help.

# Design pattern and structure
![System structure](http://cherrieblog.tw/project/UML_small.png)

### Description
This project uses MVC pattern to build the whole structure.

# Acknowledgment
- Supervisor: Dr. David Bernhard (University of Bristol)
