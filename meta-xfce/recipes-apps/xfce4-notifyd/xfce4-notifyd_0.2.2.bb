DESCRIPTION = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-notifyd"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui xfconf gtk+ dbus dbus-glib"

PR = "r0"

inherit xfce

# SRC_URI must follow inherited one
SRC_URI = "http://archive.xfce.org/src/apps/${PN}/${@'${PV}'[0:3]}/${PN}-${PV}.tar.bz2"

FILES_${PN} += " \
    ${libdir}/xfce4/notifyd \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
"

SRC_URI[md5sum] = "8687fb7a0f270231ada265e363b6ffcc"
SRC_URI[sha256sum] = "b66e68dfc2164bcf479acd3c7e8b6f83065d23aef988535e2db3506d06a39168"
