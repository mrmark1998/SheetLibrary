# MySheet
Sheet music app that allows the user to create, read, update, and delete entries on a MySQL server

<img src="https://github.com/mrmark1998/SheetLibrary/blob/master/example1.png">

I created this app to easily organize all of the PDF sheet music that I had cluttered up on my computer.  It is currently in the works, but the idea is to make it convenient to access my sheet music.  It was created using:
- Intellij IDE
- JavaFX
- SceneBuilder
- XAMPP server with Apache/MySQL

11/20/22
- Finished the SQL logistics in the program
- Added Favorite Sheets database
- Created Dynamic Search capabilities with improved search
- Joined the SQL tables to show user their favorites

To Do:
- Make user favorites have Star in main list
- Make artist page slightly different so they can upload files
- Make admin page slightly different so they can enter admin mode
- Make categories for the sheets and be able to search by category too.

This was a pretty big update, and I'd say I've accomplished quite a lot so far.  Most of the features are complete, so I just need to add some additional features.  Here is a screenshot of the user GUI:


<img src="https://github.com/mrmark1998/SheetLibrary/blob/master/example4.png">
<img src="https://github.com/mrmark1998/SheetLibrary/blob/master/example5.png">


11/19/22
- Created User GUI that looks sleek and modern with several tabs and options

11/7/22
- Starting to set up the program to be able to include users and types(user, artist, admin)
- Login System is functioning and users table created
- Created several different views - user admin panel, login, register
- Created new logo with new name - MySheet<br>
To Do:
- Create a search functionality to be able to easily search up any data and find sheets easily
- User can add "favorites" and later bring up their favorite sheets (through SQL VIEW command?). Probably need to make another SQL table to join them by IDs.
- Limit Artists and Admins to being able to upload sheet music

11/2/22
- Optimized the program's logic
- Did extensive bug tracking and debugged many bugs from the program.
- Mainly added conditional clauses, but was able to clean up a lot of the code

11/1/22
- Created connections between client and server using Apache Commons Net FTP Protocol
- User can now Upload/Download/Open/Download PDF of any record 
- Program is mostly working but needs to be polished with some bugs here and there regarding SQL update

10/30-10/31/22 -
- Created app 

<img src="https://github.com/mrmark1998/SheetLibrary/blob/master/example2.png">

Here are the implementation details that I would like the app to do in the future:
- Create a socket with an online server to upload PDFs and access all - done 11/1
- Be able to search and organize by Composer and Genre(make new column in SQL)
- Clean up GUI

<img src="https://github.com/mrmark1998/SheetLibrary/blob/master/example3.PNG">
