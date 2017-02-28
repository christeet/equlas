<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0">
    
    <xsl:output method="xhtml" indent="yes"/>
    
    <!-- page layout -->
    <xsl:template name="pageMaster">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="first" page-height="297mm" page-width="210mm"
                margin-top="15mm" margin-bottom="5mm" margin-left="25mm" margin-right="25mm">
                <fo:region-body margin-top="25mm" margin-bottom="10mm"/>
                <fo:region-before extent="25mm" region-name="before"/>
                <fo:region-after extent="10mm" region-name="after"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
    </xsl:template>
    <xsl:template name="pageSequence">
        <fo:page-sequence master-reference="first">
            <fo:static-content flow-name="before">
                <fo:block>
                    <fo:external-graphic src="logo.png" content-height="20mm"/>
                </fo:block>
            </fo:static-content>
            <fo:static-content flow-name="after">
                <fo:block font-size="8pt" font-family="Arial" color="gray">
                    Berner Fachhochschule | Haute école spécialisée bernoise | Bern University of Applied Sciences
                </fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body">
                <fo:block>
                    <xsl:apply-templates />
                </fo:block>
            </fo:flow>
        </fo:page-sequence>
    </xsl:template>
    
    <xsl:template match="students">
        <fo:root>
            <xsl:call-template name="pageMaster"/>
            <xsl:call-template name="pageSequence"/>
        </fo:root>
        
            <xsl:apply-templates select="student" />
    </xsl:template>
    
    <xsl:template match="student">
        <fo:block page-break-before="always"/>
        <div class="emptyLines3" />
        <h2>Leistungsnachweis</h2>
        <fo:block space-before="15mm"/>
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
        <div><img src="./resources/xml/images/schmidhauser.jpg" /></div>
        <div>Arno Schmidhauser</div>
        <div>Leiter Weiterbildung</div>
        <div>Technik und Informatik</div>
        
    </xsl:template>
    <!-- styles -->
    <xsl:attribute-set name="normal">
        <xsl:attribute name="font-family">Arial</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="line-height">15pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="heading1" use-attribute-sets="standart">
        <xsl:attribute name="font-size">20pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">28pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="heading2" use-attribute-sets="standart">
        <xsl:attribute name="font-size">15pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">20pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="standart">
        <xsl:attribute name="font-family">Arial</xsl:attribute>
        <xsl:attribute name="font-size">14pt</xsl:attribute>
        <xsl:attribute name="line-height">22pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>
</xsl:stylesheet>

