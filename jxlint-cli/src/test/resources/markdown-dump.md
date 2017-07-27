Rules for jxlint - $$$VERSION$$$
========================

Unique attribute
----------------
**Summary** : Attributes within a tag must be unique.

**Category** : PERFORMANCE

**Severity** : ERROR

**Enabled by default?** : yes


**Detailed description** :

Attributes within an XML tag must be unique. That is, <tag a="x" a="y"> is invalid.

---

XML version specified
---------------------
**Summary** : Version of XML must be specified.

**Category** : LINT

**Severity** : FATAL

**Enabled by default?** : yes


**Detailed description** :

The XML version should be specified. For example, <?xml version="1.0" encoding="UTF-8"?>.

---

XML encoding specified
----------------------
**Summary** : Encoding of the XML must be specified.

**Category** : LINT

**Severity** : WARNING

**Enabled by default?** : yes


**Detailed description** :

The XML encoding should be specified. For example, <?xml version="1.0" encoding="UTF-8"?>.

---

Author tag specified
--------------------
**Summary** : author.xml files must contain a valid root-element <author> tag.

**Category** : STYLE

**Severity** : WARNING

**Enabled by default?** : no


**Detailed description** :

For style purposes, every author.xml file must contain an <author> tag as the
root element. This tag should also have the 'name' and 'creationDate'
attributes. For example:

    <?xml version="1.0" encoding="UTF-8">
    <author name="Steve Holt" creationDate="2013-09-28">
      .. content ..
    </author>




