pinyinspliter
=============

This project is a Chinese pinyin spliter

The features it have:
1. split Chinese pinyin sequence
2. detect the matched Chinese token with splitted pinyin

Basic working mechanism:
1. split input string with non-pinyin character, e.g. woai1beijing -> woai 1 beijing
2. split the Chinese pinyin char sequence with max forward match algorithm, e.g. woaibeijing -> wo ai bei jing
