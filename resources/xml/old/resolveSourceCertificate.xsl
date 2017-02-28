<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:h="http://www.w3.org/1999/xhtml" xmlns:iqs="http://iqs.ti.bfh.ch" version="2.0">
    
    <xsl:output method="xhtml" indent="yes"/>
    
    <xsl:template match="students">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title>Certificate</title>
            </head>
            <body>
              <xsl:apply-templates select="student" />
            </body>
        </html>
    </xsl:template>
    <xsl:template match="student">
        <div class="pageBreak" />
        <div class="emptyLines3" />
        <div class="standart">
            Die Berner Fachhochschule
        </div>    
        <div class="standart">
            erteilt
        </div>
        <div class="emptyLines6" />
        <div class="normal-center">
            <xsl:choose>
                <xsl:when test="sex = 'm'">Herr</xsl:when>
                <xsl:when test="sex = 'f'">Frau</xsl:when>
            </xsl:choose>
        </div>
        <h1><xsl:value-of select="firstName"/><xsl:text> </xsl:text><xsl:value-of select="lastName"/></h1>
        <div class="normal-center">
            <xsl:text> von </xsl:text><xsl:value-of select="placeOfOrigin"/><xsl:text>, geboren am </xsl:text><xsl:value-of select="format-date(dateOfBirth, '[D01]. [MNn] [Y0001]')"/>
            <br />
            <xsl:text> das </xsl:text>
        </div>
        <div class="emptyLines6" />
        <h1>Certificate of Advanced Studies BFH in</h1>
        <h1><xsl:value-of select="module/name" /></h1>
        <div class="emptyLines6" />
        <div class="normal-center">
            <xsl:text>Das CAS dauerte vom </xsl:text><xsl:value-of select="format-date(module/startDate, '[D01]. [MNn] [Y0001]')"/><xsl:text> bis zum </xsl:text>
            <xsl:value-of select="format-date(module/endDate, '[D01]. [MNn] [Y0001]')"/> und entsprach einem Arbeitspensum von 12 ECTS.
        </div>
        <div class="emptyLines6" />
        <div class="emptyLines6" />
        <div class="normal"><xsl:text>Bern, </xsl:text><xsl:value-of select="format-dateTime(current-dateTime(), '[D01]. [MNn] [Y0001]')"/></div>
        <div><img src="./schmidhauser.jpg" /></div>
        <div class="normal">Arno Schmidhauser</div>
        <div class="normal">Leiter Weiterbildung</div>
        <div class="normal">Technik und Informatik</div>
    </xsl:template>
</xsl:stylesheet>

