#!/usr/bin/env python
# -*- coding: utf-8 -*- #
from __future__ import unicode_literals

AUTHOR = 'Daw-Ran Liou'
SITEURL = 'http://localhost:8000'
SITENAME = '%s\'s Blog' % AUTHOR
SITETITLE = AUTHOR
SITESUBTITLE = 'Software Developer - Maker'
SITEDESCRIPTION = '%s\'s Thoughts and Writings' % AUTHOR
SITELOGO = '//en.gravatar.com/userimage/99964636/f9367cffe912e77fa93af6784d93b99e.jpg?size=120'
FAVICON = '/images/favicon.ico'
PYGMENTS_STYLE = 'monokai'

PATH = 'content'

TIMEZONE = 'America/Los_Angeles'

DEFAULT_LANG = 'en'

# Feed generation is usually not desired when developing
FEED_ALL_ATOM = 'feeds/all.atom.xml'
CATEGORY_FEED_ATOM = 'feeds/%s.atom.xml'
TRANSLATION_FEED_ATOM = None
AUTHOR_FEED_ATOM = None
AUTHOR_FEED_RSS = None

MAIN_MENU = True

LINKS = (
        # ('You can modify those links in your config file', '#'),
)

# Social widget
SOCIAL = (
        ('linkedin', 'https://www.linkedin.com/in/randyliou'),
        ('twitter', 'https://twitter.com/dawranliou'),
        ('github', 'https://github.com/dawran6'),
        ('medium', 'https://medium.com/@dawran6'),
        ('rss', '//dawranliou.com/feeds/all.atom.xml')
)

MENUITEMS = (
        ('Archives', '/archives.html'),
        ('Categories', '/categories.html'),
        ('Tags', '/tags.html'),
)

CC_LICENSE = {
    'name': 'Creative Commons Attribution-ShareAlike',
    'version': '4.0',
    'slug': 'by-sa'
}

COPYRIGHT_YEAR = 2016

DEFAULT_PAGINATION = 10

PLUGIN_PATHS = ['pelican-plugins']
PLUGINS = ['sitemap', 'post_stats']

SITEMAP = {
    'format': 'xml',
    'priorities': {
        'articles': 0.6,
        'indexes': 0.6,
        'pages': 0.5,
    },
    'changefreqs': {
        'articles': 'monthly',
        'indexes': 'daily',
        'pages': 'monthly',
    }
}

# Uncomment following line if you want document-relative URLs when developing
# RELATIVE_URLS = True

STATIC_PATHS = ['images']

# THEME setting
THEME = 'themes/Flex'

USE_LESS = True
