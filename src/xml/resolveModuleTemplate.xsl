<?xml version="1.0" encoding="UTF-8"?>

<!-- XSL stylesheet for the resolution of the IQS elements in the XHTML template -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:iqs="http://iqs.ti.bfh.ch" xmlns:h="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml">

    <xsl:output method="xhtml" indent="yes"/>

    <xsl:param name="moduleDocument"/>
    <xsl:variable name="students" select="$moduleDocument/students"/>
	
    <xsl:template match="iqs:iteration">
        <xsl:param name="studentShortName"/>
        <xsl:variable name="iteration" select="."/>
        <xsl:for-each select="$students/student">
            <xsl:sort select="lastName"/>
            <xsl:sort select="firstName"/>
            <xsl:apply-templates select="$iteration/node() | @*">
                <xsl:with-param name="studentShortName" select="shortName"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="iqs:data">
        <xsl:param name="studentShortName"/>
        <xsl:variable name="student" select="$students/student[shortName = $studentShortName]"/>
        <xsl:choose>
            <!-- student data -->
            <xsl:when test="@id = 'studentSalutation'">
                <xsl:if test="$student/sex = 'm'">Herr</xsl:if>
                <xsl:if test="$student/sex = 'f'">Frau</xsl:if>
            </xsl:when>
            <xsl:when test="@id = 'studentName'">
                <xsl:value-of select="$student/firstName"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$student/lastName"/>
            </xsl:when>
            <xsl:when test="@id = 'studentDateOfBirth'">
                <xsl:value-of select="format-date($student/dateOfBirth, '[D]. [MNn] [Y]', 'de', (), ())"/>
            </xsl:when>
            <xsl:when test="@id = 'studentPlaceOfOrigin'">
                <xsl:value-of select="$student/placeOfOrigin"/>
            </xsl:when>

            <!-- module data -->
            <xsl:when test="@id = 'moduleName'">
                <xsl:value-of select="$student/module/name"/>
            </xsl:when>
            <xsl:when test="@id = 'moduleStartDate'">
                <xsl:value-of select="format-date($student/module/startDate, '[D]. [MNn] [Y]', 'de', (), ())"/>
            </xsl:when>
            <xsl:when test="@id = 'moduleEndDate'">
                <xsl:value-of select="format-date($student/module/endDate, '[D]. [MNn] [Y]', 'de', (), ())"/>
            </xsl:when>
            
            <!-- course data -->
            <xsl:when test="@id = 'courses'">
                <xsl:variable name="courses" select="$student/courses"/>
                <xsl:for-each select="$student/courses">
                    <tr>
                        <td><xsl:value-of select="name"/></td>
                        <td><xsl:value-of select="weight"/></td>
                        <td><xsl:value-of select="rating"/> </td>
                    </tr>
                </xsl:for-each>
                <tr>
                    <td id="bold">Gesamt Erfolg:</td>
                    <td></td>
                    <td id="bold"><xsl:value-of select="format-number(sum(for $x in $courses return sum($x/rating * $x/weight)) div sum($courses/weight), '#.0')" /></td>
                </tr>
                <tr>
                    <td id="bold">ECTS-Note:</td>
                    <td></td>
                    <td id="bold">
                        <xsl:variable name="summe" select="sum(for $x in $courses return sum($x/rating * $x/weight)) div sum($courses/weight)" />
                        <xsl:choose>
	                        <xsl:when test="$summe > 89">A</xsl:when>
	                        <xsl:when test="$summe > 79">B</xsl:when>
	                        <xsl:when test="$summe > 69">C</xsl:when>
	                        <xsl:when test="$summe > 59">D</xsl:when>
	                        <xsl:when test="$summe > 49">E</xsl:when>
	                        <xsl:when test="$summe &lt; 50">F</xsl:when>
                        </xsl:choose>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="iqs:*">
        <xsl:element name="h:div">
            <xsl:attribute name="class">
                <xsl:value-of select="local-name()"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="iqs:currentDate">
        <xsl:value-of select="format-date(current-date(), '[D]. [MNn] [Y]', 'de', (), ())"/>
    </xsl:template>

    <!-- copy function with parameters -->
    <xsl:template match="node() | @*">
        <xsl:param name="studentShortName"/>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*">
                <xsl:with-param name="studentShortName" select="$studentShortName"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
