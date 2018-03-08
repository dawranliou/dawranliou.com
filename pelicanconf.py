#!/usr/bin/env python
# -*- coding: utf-8 -*- #
from __future__ import unicode_literals

AUTHOR = 'Daw-Ran Liou'
SITEURL = 'https://dawranliou.com'
SITENAME = AUTHOR
SITETITLE = AUTHOR
BIO = 'Software Developer, Maker'
SITEDESCRIPTION = '%s\'s Thoughts and Writings' % AUTHOR
SITELOGO = '//en.gravatar.com/userimage/99964636/be9acf0f4e25c6ccdb50a2465b85aa27.jpeg?size=120'
FAVICON = '/images/favicon.ico'
PYGMENTS_STYLE = 'github'

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
PLUGINS = [
    #'sitemap',
    #'post_stats',
]

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

STATIC_PATHS = [
    'images',
    'favicon',
]
EXTRA_PATH_METADATA = {
    'favicon/android-icon-144x144.png': {'path': 'android-icon-144x144.png'},
    'favicon/android-icon-192x192.png': {'path': 'android-icon-192x192.png'},
    'favicon/android-icon-36x36.png': {'path': 'android-icon-36x36.png'},
    'favicon/android-icon-48x48.png': {'path': 'android-icon-48x48.png'},
    'favicon/android-icon-72x72.png': {'path': 'android-icon-72x72.png'},
    'favicon/android-icon-96x96.png': {'path': 'android-icon-96x96.png'},
    'favicon/apple-icon-114x114.png': {'path': 'apple-icon-114x114.png'},
    'favicon/apple-icon-120x120.png': {'path': 'apple-icon-120x120.png'},
    'favicon/apple-icon-144x144.png': {'path': 'apple-icon-144x144.png'},
    'favicon/apple-icon-152x152.png': {'path': 'apple-icon-152x152.png'},
    'favicon/apple-icon-180x180.png': {'path': 'apple-icon-180x180.png'},
    'favicon/apple-icon-57x57.png': {'path': 'apple-icon-57x57.png'},
    'favicon/apple-icon-60x60.png': {'path': 'apple-icon-60x60.png'},
    'favicon/apple-icon-72x72.png': {'path': 'apple-icon-72x72.png'},
    'favicon/apple-icon-76x76.png': {'path': 'apple-icon-76x76.png'},
    'favicon/apple-icon-precomposed.png': {'path': 'apple-icon-precomposed.png'},
    'favicon/apple-icon.png': {'path': 'apple-icon.png'},
    'favicon/favicon-16x16.png': {'path': 'favicon-16x16.png'},
    'favicon/favicon-32x32.png': {'path': 'favicon-32x32.png'},
    'favicon/favicon-96x96.png': {'path': 'favicon-96x96.png'},
    'favicon/favicon.ico': {'path': 'favicon.ico'},
    'favicon/ms-icon-144x144.png': {'path': 'ms-icon-144x144.png'},
    'favicon/ms-icon-150x150.png': {'path': 'ms-icon-150x150.png'},
    'favicon/ms-icon-310x310.png': {'path': 'ms-icon-310x310.png'},
    'favicon/ms-icon-70x70.png': {'path': 'ms-icon-70x70.png'},
    'favicon/manifest.json': {'path': 'manifest.json'},
}

# THEME setting
THEME = 'hyde'

USE_LESS = True
