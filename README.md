Project that shows Scala and GWT work together to compile a few sample applications.

In order to run it you just need to type `ant` assuming you have [Apache Ant](http://ant.apache.org/)
installed. After compilation is done open one of the html files from `war/` directory in a browser.

**NOTE**: Due to security restrictions samples will not work in Google Chrome if
you load them from local file system. If you want to use Chrome, you must load files from
`war/` directory using http server. E.g. you can use python's built-in http server:
`python -m SimpleHTTPServer 8000` and point your browser to `http://localhost:8000/war/`.

This project uses custom versions of GWT and Scala. These are included in the 'lib/' directory in
releases. For snapshots, consult [scalagwt@googlegroups.com](http://groups.google.com/group/scalagwt).

Have fun!
