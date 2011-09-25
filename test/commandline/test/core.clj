(ns commandline.test.core
  (:import [org.apache.commons.cli Option])
  (:use clojure.test
        commandline.core))

(deftest test-make-option
  (let [option (make-option 'a 'all "do not hide entries starting with .")]
    (is (isa? (class option) Option))
    (is (nil? (.getArgName option)))
    (is (= -1 (.getArgs option)))
    (is (= "do not hide entries starting with ." (.getDescription option)))
    (is (= "all" (.getLongOpt option)))
    (is (= "a" (.getOpt option)))
    (is (nil? (.getValue option)))
    (is (= \  (.getValueSeparator option)))
    (is (not (.isRequired option))))
  (let [option (make-option nil 'block-size "use SIZE-byte blocks" :integer "SIZE" false)]
    (is (isa? (class option) Option))
    (is (= "SIZE" (.getArgName option)))
    (is (= 1 (.getArgs option)))
    (is (= "use SIZE-byte blocks" (.getDescription option)))
    (is (= "block-size" (.getLongOpt option)))
    (is (nil? (.getOpt option)))
    (is (nil? (.getValue option)))
    (is (= \  (.getValueSeparator option)))
    (is (not (.isRequired option))))
  (is (= (make-option 'a 'all "do not hide entries starting with .")
         (make-option :a :all "do not hide entries starting with .")
         (make-option "a" "all" "do not hide entries starting with ."))))

(deftest test-make-parser
  (let [parser (make-parser :gnu)]
    (is parser)))

(deftest test-with-command-line
  (testing "ant options"
    (with-commandline :gnu ["-help" "-projecthelp" "-version" "-verbose" "-debug" "-emacs" "-logfile" "logfile" "-logger" "java.util.logging.Logger"]
      [[help nil "print this message"]
       [projecthelp nil "print project help information"]
       [version nil "print the version information and exit"]
       [quiet nil "be extra quiet"]
       [verbose nil "be extra verbose"]
       [debug nil "print debugging information"]
       [emacs nil "produce logging information without adornments"]
       [logfile nil "use given file for log" :file "file"]
       [logger nil "the class which is to perform logging" :class "classname"]
       [listener nil "add an instance of class as a project listener" :class "classname"]
       [buildfile nil "use given buildfile" :file "file"]]
      (is help)
      (is projecthelp)
      (is version)
      (is verbose)
      (is debug)
      (is emacs)
      (is (= (java.io.File. "logfile") logfile))
      (is (= java.util.logging.Logger logger))
      (is (nil? listener))
      (is (nil? buildfile))
      (with-out-str test
        (print-usage "ant")
        (print-help "ant"))))
  (testing "ls options"
    (with-commandline :gnu ["-a" "--all" "-A" "--almost-all" "-b" "--escape" "--block-size" "10" "-c"]
      [[a all "do not hide entries starting with ."]
       [A almost-all "do not list implied . and .."]
       [b escape "print octal escapes for nongraphic characters"]
       [nil block-size "use SIZE-byte blocks" :integer "SIZE"]
       [B ignore-backups "do not list implied entried ending with ~"]
       [c nil "with -lt: sort by, and show, ctime (time of last modification of file status information) with -l:show ctime and sort by name otherwise: sort by ctime"]
       [C nil "list entries by columns"]]
      (is a)
      (is all)
      (is A)
      (is almost-all)
      (is b)
      (is escape)
      (is (= 10 block-size))
      (is (not B))
      (is (not ignore-backups))
      (is c)
      (is (not C))
      (with-out-str test
        (print-usage "ls")
        (print-help "ls")))))
