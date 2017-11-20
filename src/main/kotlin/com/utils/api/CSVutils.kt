package com.utils.api

import com.janus.utilities.loggerFor
import net.corda.core.messaging.CordaRPCOps
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.io.IOUtils
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@CrossOriginResourceSharing(allowAllOrigins = true,
        allowOrigins = arrayOf("http://localhost:4200"),
        allowHeaders = arrayOf("origin", "content-type", "accept", "authorization", "activityId"),
        exposeHeaders = arrayOf("origin", "content-type", "accept", "authorization", "activityId"))
@Path("com.janus")
class CSVutils(val services: CordaRPCOps){
    private companion object {
        val logger = loggerFor<JanusApi>()
    }
    @POST
    @Path("json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun createJSONDocument(@Context req: HttpServletRequest): Response {
        logger.info("createDocument() ==> begin")
        var json=""
        val isMultiPart = ServletFileUpload.isMultipartContent(req)
        if (isMultiPart) {
            logger.info("createDocument() ==> isMultiPart")
            val upload = ServletFileUpload()
            val iterator = upload.getItemIterator(req)
            if (!iterator.hasNext()) {
                logger.info("createDocument() ==> zero uploads")
            }
            while (iterator.hasNext()) {
                val item = iterator.next()
                logger.info("createDocument() - Receiving    ==> ${item.name}")
                logger.info("createDocument() - Content Type ==> ${item.contentType}")
                var inputStream = item.openStream()
                val writer = StringWriter()
                IOUtils.copy(inputStream, writer, "UTF-8");
                val csv = writer.toString()

                var allLines=csv.split("\r","\n").toMutableList()
                var headList = parseLine(allLines[0])
                var linelist = mutableListOf(mutableListOf<String>())
                for(i in allLines.indices){
                    if(i == 0) continue
                    linelist.add(parseLine(allLines[i]).toMutableList())
                }
                json=csvToJson(headList, linelist)
            }
        } else
            logger.info("createDocument() ==> NOT isMultiPart")

        logger.info("createDocument() ==> end")
        val response = Response.status(Response.Status.OK).entity(json).build()
        return response
    }

    @POST
    @Path("typescript")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun createTypescriptDocument(@Context req: HttpServletRequest): Response {
        logger.info("createDocument() ==> begin")
        var json=""
        val isMultiPart = ServletFileUpload.isMultipartContent(req)
        if (isMultiPart) {
            logger.info("createDocument() ==> isMultiPart")
            val upload = ServletFileUpload()
            val iterator = upload.getItemIterator(req)
            if (!iterator.hasNext()) {
                logger.info("createDocument() ==> zero uploads")
            }
            while (iterator.hasNext()) {
                val item = iterator.next()
                logger.info("createDocument() - Receiving    ==> ${item.name}")
                logger.info("createDocument() - Content Type ==> ${item.contentType}")
                var inputStream = item.openStream()
                val writer = StringWriter()
                IOUtils.copy(inputStream, writer, "UTF-8");
                val csv = writer.toString()

                var allLines=csv.split("\r","\n").toMutableList()
                var headList = parseLine(allLines[0])
                var linelist = mutableListOf(mutableListOf<String>())
                for(i in allLines.indices){
                    if(i == 0) continue
                    linelist.add(parseLine(allLines[i]).toMutableList())
                }
                json=csvToTypescript(item.name, headList)
            }
        } else
            logger.info("createDocument() ==> NOT isMultiPart")

        logger.info("createDocument() ==> end")
        val response = Response.status(Response.Status.OK).entity(json).build()
        return response
    }

    @POST
    @Path("angular")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun createAngularDocument(@Context req: HttpServletRequest): Response {
        logger.info("createDocument() ==> begin")
        var json=""
        val isMultiPart = ServletFileUpload.isMultipartContent(req)
        if (isMultiPart) {
            logger.info("createDocument() ==> isMultiPart")
            val upload = ServletFileUpload()
            val iterator = upload.getItemIterator(req)
            if (!iterator.hasNext()) {
                logger.info("createDocument() ==> zero uploads")
            }
            while (iterator.hasNext()) {
                val item = iterator.next()
                logger.info("createDocument() - Receiving    ==> ${item.name}")
                logger.info("createDocument() - Content Type ==> ${item.contentType}")
                var inputStream = item.openStream()
                val writer = StringWriter()
                IOUtils.copy(inputStream, writer, "UTF-8");
                val csv = writer.toString()

                var allLines=csv.split("\r","\n").toMutableList()
                var headList = parseLine(allLines[0])
                var linelist = mutableListOf(mutableListOf<String>())
                for(i in allLines.indices){
                    if(i == 0) continue
                    linelist.add(parseLine(allLines[i]).toMutableList())
                }
                json=csvToAngular(item.name, headList)
            }
        } else
            logger.info("createDocument() ==> NOT isMultiPart")

        logger.info("createDocument() ==> end")
        val response = Response.status(Response.Status.OK).entity(json).build()
        return response
    }

    fun parseLine(line:String):List<String>{
        val pattern = "[^A-Za-z0-9 _/.]".toRegex()
        var list =line.split(',').toMutableList()
        for(i in list.indices){
            list[i] = list[i].replace(pattern,"")
        }
        return list
    }

    fun csvToJson(headerList: List<String>, lineList:MutableList<MutableList<String>>):String {
        var json = StringBuilder()
        json.appendln("[")
        val maxColumns = headerList.size - 1
        var maxRows = lineList.size
        var rows = 0
        lineList.forEach{
            var fields = it
            if (fields.size <= 1) {
                --maxRows
                return@forEach
            }
            json.appendln("{")
            var cols = 0
            headerList.forEach {

                json.append("\"")
                json.append(it.replace(' ', '_'))
                json.append("\"")
                json.append(" : ")
                json.append("\"")

                if(cols < fields.size) {
                    json.append(fields[cols++])
                }

                if (cols <= maxColumns) {
                    json.append("\"")
                    json.appendln(",")
                } else {
                    json.appendln("\"")
                }
            }

            if(++rows < maxRows) {
                json.appendln("},")
            } else {
                json.appendln("}")
            }
        }
        json.appendln("]")

        return json.toString()
    }

    fun csvToTypescript(className: String, headerList: List<String>): String{
        var ts = StringBuilder()
        ts.append("export class ")
        ts.append( className)
        ts.appendln(" {")
        headerList.forEach{
            ts.append(it)
            ts.appendln(": string;")
        }
        ts.appendln("}")
        return ts.toString()
    }

    fun csvToAngular(className: String, headerList: List<String>): String{
        var ng = StringBuilder()

        ng.appendln("<table class=\"table table-striped table-border table-hover\">")
        ng.appendln("<br/>")
        ng.appendln("<tr>")
        headerList.forEach{
            ng.append("<th>")
            ng.append(it)
            ng.appendln("</th>")
        }
        ng.appendln("</tr>")
        ng.append("<tr *ngFor=\"let g of ")
        ng.append(className)
        ng.appendln("\">")
        headerList.forEach {
            ng.append("<td>{{g.")
            ng.append(it)
            ng.appendln("}}</td>")
        }
        ng.appendln("</tr>")
        ng.appendln("</table>")

        //<tr *ngFor="let spv of spvResponse">


        return ng.toString()
    }
}