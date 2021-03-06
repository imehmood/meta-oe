DESCRIPTION = "Epdf is the glue between EFL and libpoppler"

DEPENDS = "poppler evas ecore"
PV = "0.1.0+svnr${SRCPV}"
PR = "r5"
SRCREV = "${EFL_SRCREV}"

inherit efl

LICENSE = "GPLv2 LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6 \
"

SRC_URI = "${E_SVN}/trunk/PROTO;module=${SRCNAME};proto=http;scmdata=keep \
           file://epdf.poppler-0.16.api.change.patch \
"
S = "${WORKDIR}/${SRCNAME}"

EXTRA_OECONF = "\
    --enable-poppler \
    --disable-ewl \
    --disable-mupdf \
"

# Some upgrade path tweaking, as in evas
AUTO_LIBNAME_PKGS = ""

