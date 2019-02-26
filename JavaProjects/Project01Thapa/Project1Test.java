// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

public class Project1Test {
    
    public static void main (String[] args){


    Project1 Testproject = new Project1 ("IDE and Text Editor");
    Testproject.appendTag (new HeadingTag("List of Integrated Development "
            + "Environment (IDE) and Text Editor for Java", 2));        
    Testproject.appendTag (new TableTag("lightblue")); //creates table with
                                                      //lightblue background

   // Table Head
    TableRowTag HeadRow = new TableRowTag();             
    HeadRow.appendTabRow(new TableHeadItems("Name of IDE"));
    HeadRow.appendTabRow(new TableHeadItems("Cost"));
    HeadRow.appendTabRow(new TableHeadItems("Open Source"));
    HeadRow.appendTabRow(new TableHeadItems("Description"));

    //Table Row 1
    TableDataTag Row1 = new TableDataTag();
    AnchorTag Tag1 = new AnchorTag("NetBeans IDE is written in Java and"
                                   +" intended for development in Java, but"
                                   +" also  supports other languages, such as "
                                   + "PHP,C/C++ and HTML5. It is cross platform"
                                   +" and can be used application development"
                                   +" on Windows, Mac, Linux, and Solaris "
                                   +" operating systems.","Click here for more"
                                   +" info", "https://netbeans.org/");

    Row1.appendTabData(new TableDataItem ("NetBeans"));
    Row1.appendTabData(new TableDataItem ("Free"));
    Row1.appendTabData(new TableDataItem ("Yes"));
    Row1.appendTabData(new TableDataItem (Tag1.toString()));

    //Table Row 2        
    TableDataTag Row2 = new TableDataTag();
    AnchorTag Tag2 = new AnchorTag("Eclispe is a most widely used Java IDE"
                                   +" It i mostly written in Java and used to "
                                   +" create various cross platform Java"
                                   +" applications for use on mobile, web,"
                                   +" desktop and enterprise domains. Through "
                                   +" the use of its plug in featuers, it can "
                                   +" be used to develop applications in other"
                                   +" programming languages such as C, C++, "
                                   +" COBOL, D, Fortran, Haskell, JavaScript",
                                    "Click here for more info",
                                     "https://eclipse.org/");

    Row2.appendTabData(new TableDataItem ("Eclipse"));
    Row2.appendTabData(new TableDataItem ("Free"));
    Row2.appendTabData(new TableDataItem ("Yes"));
    Row2.appendTabData(new TableDataItem (Tag2.toString()));

     //Table Row 3               
    TableDataTag Row3 = new TableDataTag();
    AnchorTag Tag3 = new AnchorTag("IntelliJ Community Edition is free Java IDE"
                                    +" is a free Java IDE is mainly used for"
                                    +" Android app development, Java SE and"
                                    +" Java programming. There is a limited "
                                    +" feature available in community edition"
                                    +" but other features are available with"
                                    +" the purchase of license", "Click here" 
                                    +" for more info",
                                      "https://www.jetbrains.com");
    
    Row3.appendTabData(new TableDataItem("IntelliJ IDEA Community Edition"));
    Row3.appendTabData(new TableDataItem ("Free(Community Edition)"));
    Row3.appendTabData(new TableDataItem ("Yes"));
    Row3.appendTabData(new TableDataItem (Tag3.toString()));

    //Table Row 4
    TableDataTag Row4 = new TableDataTag();
    AnchorTag Tag4 = new AnchorTag("JCreator is written in C++ and is faster,"
                                    +" more efficient and more reliable than "
                                    +" other Java IDE. It provides the user"
                                    +" with a wide range of functionality such"
                                    +" as Project management, project templates"
                                    +" code-completion, debugger interface "
                                    +" e.t.c.","Click here for more info",
                                       "http://www.jcreator.com/");

    Row4.appendTabData(new TableDataItem ("JCreator"));
    Row4.appendTabData(new TableDataItem ("Free"));
    Row4.appendTabData(new TableDataItem ("Yes"));
    Row4.appendTabData(new TableDataItem (Tag4.toString()));
     
    //Table Row 5
    TableDataTag Row5 = new TableDataTag();
    AnchorTag Tag5 = new AnchorTag("It is a lightweight development"
                                    +" environment for writing Java programs." 
                                    +" It is designed primarily for students," 
                                    +" providing an intuitive interface and the"
                                    +" ability to interactively evaluate Java"
                                    +" code. It also includes powerful features"
                                    +" for more advanced users.","Click here"
                                    +" for more info","http://www.drjava.org/");

    Row5.appendTabData(new TableDataItem ("DrJava"));
    Row5.appendTabData(new TableDataItem ("Free"));
    Row5.appendTabData(new TableDataItem ("Yes"));
    Row5.appendTabData(new TableDataItem (Tag5.toString()));
    
    //Table Row 6
    TableDataTag Row6 = new TableDataTag();
    AnchorTag Tag6 = new AnchorTag("Bluej is an IDE for Java programming"
                                   +" language. It has smaller and simpler"
                                   +" interface unlike NetBeans or Eclipse."
                                   +" It is designed for beginners, students "
                                   +" and for teaching purposes.","Click here"
                                   +" for more info",
                                    "http://www.bluej.org/");

    Row6.appendTabData(new TableDataItem ("BlueJ"));
    Row6.appendTabData(new TableDataItem ("Free"));
    Row6.appendTabData(new TableDataItem ("Yes"));
    Row6.appendTabData(new TableDataItem (Tag6.toString()));

    //Table Row 7
    TableDataTag Row7 = new TableDataTag();
    AnchorTag Tag7 = new AnchorTag("It is a text editior with hundreds of"
                                    +" person-years of development behind it."
                                    +" It is written in Java, so it runs on "
                                    +" Mac,Unix, VMS and Windows. It is Highly "
                                    +" configurable and customizable.",
                                     "Click here for more info",
                                    "http://www.jedit.org/");

    Row7.appendTabData(new TableDataItem ("jEdit"));
    Row7.appendTabData(new TableDataItem ("Free"));
    Row7.appendTabData(new TableDataItem ("Yes"));
    Row7.appendTabData(new TableDataItem (Tag7.toString()));
    
    //Table Row 8

    TableDataTag Row8 = new TableDataTag();
    AnchorTag Tag8 = new AnchorTag("Notepad++ is a source code editor that"
                                   +" supports many languages such as  C, C++,"
                                   +" Java, C#, XML, HTML, PHP,and JavaScript."
                                   +" It is written in C++ and runs on "
                                   + "MS Windows only.", "Click here for more ",
                                   "http://www.jgrasp.org");

    Row8.appendTabData(new TableDataItem ("Notepad++"));
    Row8.appendTabData(new TableDataItem ("Free"));
    Row8.appendTabData(new TableDataItem ("Yes"));
    Row8.appendTabData(new TableDataItem (Tag8.toString()));
    
    //Table Row 9

    TableDataTag Row9 = new TableDataTag();
    AnchorTag Tag9 = new AnchorTag("JSource is a small Java IDE written in Java"
                                   +" and is lightweight. It supports syntax"
                                   +" highlighting for multiple languages and"
                                   +" Java Swing Components. It has a light but"
                                   +" powerful editor that allows creating,"
                                   +" editing, compiling, and running Java"
                                   +" files.","Click here for more info",
                                   "https://sourceforge.net/projects/jsource/");

    Row9.appendTabData(new TableDataItem ("JSource"));
    Row9.appendTabData(new TableDataItem ("Free"));
    Row9.appendTabData(new TableDataItem ("Yes"));
    Row9.appendTabData(new TableDataItem (Tag9.toString()));
    
    //Table Row 10
    TableDataTag Row10 = new TableDataTag();
    AnchorTag Tag10 = new AnchorTag("JDeveloper is an IDE from Oracle"
                                    +" Corporation for developing java based "
                                    +" applications. It offers 3 different"
                                    +" editions Java Edition, Studio Edition"
                                    +" and J2EE edition. It can be used for "
                                    +" coding, debugging, optimization and "
                                    +" profiling to deploying.","Click here for"
                                    +" more info","http://www.oracle.com/"
                                    +" technetwork/developer-tools/"
                                    +" jdev/overview/index-094652.html");
    Row10.appendTabData(new TableDataItem ("JDeveloper"));
    Row10.appendTabData(new TableDataItem ("Free"));
    Row10.appendTabData(new TableDataItem ("Yes"));
    Row10.appendTabData(new TableDataItem (Tag10.toString()));

    Testproject.appendTag(HeadRow);    
    Testproject.appendTag(Row1);          
    Testproject.appendTag(Row2);
    Testproject.appendTag(Row3);
    Testproject.appendTag(Row4);
    Testproject.appendTag(Row5);
    Testproject.appendTag(Row6);
    Testproject.appendTag(Row7);
    Testproject.appendTag(Row8);
    Testproject.appendTag(Row9);
    Testproject.appendTag(Row10);         

    System.out.println(Testproject);      
   }
}
