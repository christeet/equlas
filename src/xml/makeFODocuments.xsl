<?xml version="1.0" encoding="UTF-8"?>

<!-- XSL stylesheet for the transformation of an XHTML document to a XSL-FO document -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:h="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml">

    <!-- page layout -->
    <xsl:template name="pageMaster">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="first" page-height="297mm" page-width="210mm"
                                               margin-top="15mm" margin-bottom="5mm" margin-left="25mm" margin-right="25mm">
                <fo:region-body margin-top="35mm" margin-bottom="10mm"/>
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
                    <xsl:apply-templates/>
                </fo:block>
            </fo:flow>
        </fo:page-sequence>
    </xsl:template>


    <!-- XHTML templates -->
    <xsl:template match="h:html">
        <fo:root>
            <xsl:call-template name="pageMaster"/>
            <xsl:call-template name="pageSequence"/>
        </fo:root>
    </xsl:template>
    <xsl:template match="h:head"/>
    <xsl:template match="h:body">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="h:h1">
        <xsl:element name="fo:block" use-attribute-sets="heading1">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:h1[@class='center']">
        <xsl:element name="fo:block" use-attribute-sets="heading1-center">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:h2">
        <xsl:element name="fo:block" use-attribute-sets="heading2">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:div[@class='standart']">
        <xsl:element name="fo:block" use-attribute-sets="standart">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:div[@class='normal']">
        <xsl:element name="fo:block" use-attribute-sets="normal">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:div[@class='normal-center']">
        <xsl:element name="fo:block" use-attribute-sets="normal-center">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:div">
        <xsl:element name="fo:block" use-attribute-sets="normal">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="h:img">
        <fo:external-graphic>
            <xsl:attribute name="src">
                <xsl:value-of select="@src"/>
            </xsl:attribute>
        </fo:external-graphic>
    </xsl:template>
    
    <xsl:template match="h:table">
        <fo:table table-layout="fixed" width="100%">
            <fo:table-column column-number="1" column-width="80mm"/>
            <fo:table-column column-number="2" column-width="40mm"/>
            <fo:table-column column-number="3" column-width="40mm"/>
            <fo:table-body>
                <xsl:for-each select="h:tr">
                    <xsl:choose>
                        <xsl:when test="h:th">
                            <fo:table-row>
                                <xsl:for-each select="h:th">
                                    <fo:table-cell><fo:block><fo:inline font-size="small" font-weight="bold"><xsl:apply-templates /></fo:inline></fo:block></fo:table-cell>
                                </xsl:for-each>
                            </fo:table-row>
                        </xsl:when>
                        <xsl:when test="h:td/@*">
                            <fo:table-row>
                                <xsl:for-each select="h:td">
                                    <fo:table-cell><fo:block font-size="small" font-weight="bold"><xsl:apply-templates /></fo:block></fo:table-cell>
                                </xsl:for-each>
                            </fo:table-row>
                        </xsl:when>
                        <xsl:when test="h:td">
                            <fo:table-row>
                            <xsl:for-each select="h:td">
                                <fo:table-cell><fo:block font-size="small"><xsl:apply-templates /></fo:block></fo:table-cell>
                            </xsl:for-each>
                            </fo:table-row>
                        </xsl:when>
                    </xsl:choose>
                    
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
    </xsl:template>
    
    <xsl:template match="h:div[@class='emptyLines1']">
        <fo:block space-before="5mm"/>
    </xsl:template>
    <xsl:template match="h:div[@class='emptyLines3']">
        <fo:block space-before="15mm"/>
    </xsl:template>
    <xsl:template match="h:div[@class='emptyLines6']">
        <fo:block space-before="30mm"/>
    </xsl:template>
    <xsl:template match="h:div[@class = 'pageBreak']">
        <fo:block page-break-after="always"/>
    </xsl:template>

    <!-- styles -->
    <xsl:attribute-set name="normal-center">
        <xsl:attribute name="font-family">Arial</xsl:attribute>
        <xsl:attribute name="font-size">14pt</xsl:attribute>
        <xsl:attribute name="line-height">22pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="normal">
        <xsl:attribute name="font-family">Arial</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="line-height">15pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="normal-bold">
        <xsl:attribute name="font-family">Arial</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="line-height">15pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="heading1" use-attribute-sets="standart">
        <xsl:attribute name="font-size">20pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">28pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="heading1-center" use-attribute-sets="standart">
        <xsl:attribute name="font-size">20pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">28pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
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
