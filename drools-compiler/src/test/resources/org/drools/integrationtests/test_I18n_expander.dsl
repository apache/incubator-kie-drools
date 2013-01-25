// For general i18n DSL test

// Testing 'IDEOGRAPHIC SPACE' (U+3000)
[when]名前が {firstName}=Person(name=="山本　{firstName}")
[then]メッセージ {message}=messages.add("メッセージ　" + {message});
