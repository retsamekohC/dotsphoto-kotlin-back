package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreatePhotoContentDto
import com.dotsphoto.orm.dto.PhotoContentDto
import com.dotsphoto.orm.tables.PhotoContent
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam


class PhotoContentRepository : NonUpdatableLongIdRepository<PhotoContent.Table, PhotoContentDto, CreatePhotoContentDto>() {
    override fun create(cdto: CreatePhotoContentDto): PhotoContentDto = insertAndGetDto {
        it[content] = ExposedBlob(cdto.content)
        it[compressedContent] = ExposedBlob(compressPhoto(cdto.content))
    }

    private fun compressPhoto(content: ByteArray): ByteArray {
        // если фотка меньше 700 килобайт, то храним её саму как превью, быстро будет
        if (content.size < 1024 * 700) {
            return content
        } else { // иначе сжимаем
            val imageWriter = ImageIO.getImageWritersByFormatName("jpeg")
            val jpegWriter = imageWriter.next();
            val params = jpegWriter.defaultWriteParam
            params.compressionMode = ImageWriteParam.MODE_EXPLICIT
            val coef = 1024 * 701.0f / content.size
            params.compressionQuality = coef
            val baos = ByteArrayOutputStream()
            ImageIO.createImageOutputStream(baos).use {
                jpegWriter.output = it
                val i = IIOImage(ImageIO.read(ByteArrayInputStream(content)), null, null)

                if (i.renderedImage.colorModel.hasAlpha()) {
                    val bi = asBuffered(i.renderedImage)
                    val g = bi.createGraphics()
                    g.fillRect(0, 0, i.renderedImage.width, i.renderedImage.height)
                    g.drawImage(i.renderedImage as Image, 0, 0, null)
                    g.dispose()
                    jpegWriter.write(null, IIOImage(bi.raster, null, null), params)
                } else {
                    jpegWriter.write(null, i, params)
                }
                jpegWriter.dispose()
                it.flush()
            }
            return baos.toByteArray()
        }
    }

    private fun asBuffered(rendered: RenderedImage) = BufferedImage(
        rendered.width, rendered.height,
        if (rendered.colorModel.hasAlpha()) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
    )

    override fun mapper(resultRow: ResultRow): PhotoContentDto = PhotoContentDto(
        resultRow[PhotoContent.id].value,
        resultRow[PhotoContent.content].bytes,
        resultRow[PhotoContent.compressedContent].bytes,
    )

    override fun getTable(): PhotoContent.Table = PhotoContent.Table

}