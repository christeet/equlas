<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:h="http://www.w3.org/1999/xhtml" version="2.0">
    
    <xsl:output method="xhtml" indent="yes"/>
    
    <xsl:template match="students">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title>Leistungsnachweis</title>
            </head>
            <body>
            <xsl:apply-templates select="student" />
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="student">
        <div class="pageBreak" />
        <div class="emptyLines3" />
        <h2>Leistungsnachweis</h2>
        <div class="emptyLines3" />
        <div class="normal">
            <xsl:choose>
                <xsl:when test="sex = 'm'">Herr</xsl:when>
                <xsl:when test="sex = 'f'">Frau</xsl:when>
            </xsl:choose>
        </div>
        <h1><xsl:value-of select="firstName"/><xsl:text> </xsl:text><xsl:value-of select="lastName"/></h1>
        <div class="normal">
            <xsl:text>von </xsl:text><xsl:value-of select="placeOfOrigin"/><xsl:text>, geboren am </xsl:text>
            <xsl:value-of select="format-date(dateOfBirth, '[D01]. [MNn] [Y0001]')"/>
            <br />
            <xsl:text> hat das </xsl:text>
        </div>
        <div class="emptyLines3" />
        <h1>Certificate of Advanced Studies BFH in</h1>
        <h1><xsl:value-of select="module/name" /></h1>
        <div class="emptyLines3" />
        <div class="normal">erfolgreich abgeschlossen.</div>
        <div class="emptyLines1" />
        <div class="normal">
            <xsl:text>Das CAS dauerte vom </xsl:text><xsl:value-of select="format-date(module/startDate, '[D01]. [MNn] [Y0001]')"/>
            <xsl:text> bis zum </xsl:text><xsl:value-of select="format-date(module/endDate, '[D01]. [MNn] [Y0001]')"/>
            <xsl:text> und entsprach einem Arbeitspensum von 12 ECTS.</xsl:text>
        </div>
        <div class="emptyLines3" />
        <table>
            <tr>
                <td>Kompetenznachweis</td>
                <td>Gewicht</td>
                <td>Erfolg</td>
            </tr>
            <xsl:for-each select="courses">
                <tr>
                    <td><xsl:value-of select="name"/></td>
                    <td><xsl:value-of select="weight"/></td>
                    <td><xsl:value-of select="rating"/></td>
                </tr>
            </xsl:for-each>
        </table>
        
        <div class="emptyLines6" />
        <div><xsl:text>Bern, </xsl:text><xsl:value-of select="format-dateTime(current-dateTime(), '[D01]. [MNn] [Y0001]')"/></div>
        <div><img src="./schmidhauser.jpg" /></div>
        <div>Arno Schmidhauser</div>
        <div>Leiter Weiterbildung</div>
        <div>Technik und Informatik</div>
        
    </xsl:template>
</xsl:stylesheet>

