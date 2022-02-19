(defun dawranliou-com/build-once ()
  "Build the site once."
  (interactive)
  (let ((default-directory (project-root (project-current))))
    (compile "bb build")))
