; (hello world "!")
; (hi (hello world))
(interface source
  (variable (nullable source) deeper)
)
; comment...

(datatype source_text
  (extends source describable)
  (variable string name)
  (variable (dont_describe) string content)
  (variable (nullable string) optional)
  (variable (override) (nullable source) the_source null)
  (variable (list string) list_test)
)

(enum test_enum
  foo
  bar
)

(enum operator_type
  (DOT ".")
  (ASSIGN "=")

  (variable string symbol)
)
