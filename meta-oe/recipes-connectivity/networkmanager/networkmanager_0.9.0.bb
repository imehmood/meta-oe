DESCRIPTION = "NetworkManager"
SECTION = "net/misc"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=cbbffd568227ada506640fe950a4823b"

DEPENDS = "systemd libnl1 dbus dbus-glib udev wireless-tools polkit gnutls util-linux ppp"
inherit gnome gettext

SRC_URI = "${GNOME_MIRROR}/NetworkManager/${@gnome_verdir("${PV}")}/NetworkManager-${PV}.tar.bz2 \
    file://gtk-doc.make \
"

SRC_URI[md5sum] = "f807102109e63ec708d4fd7a7f3f7deb"
SRC_URI[sha256sum] = "98d928684ab1707a8200aaeb07a648e214096b8f0fe56294a49f08c18e39714f"

S = "${WORKDIR}/NetworkManager-${PV}"

EXTRA_OECONF = " \
		--with-distro=debian \
		--with-crypto=gnutls \
		--disable-more-warnings \
        --with-dhclient=${base_sbindir}/dhclient \
        --with-iptables=${sbindir}/iptables \
"

do_configure_prepend() {
    cp ${WORKDIR}/gtk-doc.make ${S}/
    echo "EXTRA_DIST = version.xml" > gnome-doc-utils.make
    sed -i -e 's:man \\:man:' -e s:docs::g ${S}/Makefile.am
    sed -i -e /^docs/d ${S}/configure.ac
}

# Work around dbus permission problems since we lack a proper at_console
do_install_prepend() {
	sed -i -e s:deny:allow:g ${S}/src/NetworkManager.conf
	sed -i -e s:deny:allow:g ${S}/system-settings/src/nm-system-settings.conf || true
	sed -i -e s:deny:allow:g ${S}/callouts/nm-dispatcher.conf
}

do_install_append () {
	install -d ${D}/etc/dbus-1/event.d
	# Test binaries
	install -d ${D}/usr/bin
	install -m 0755 ${S}/test/.libs/nm-tool ${D}/usr/bin
	install -m 0755 ${S}/test/.libs/libnm* ${D}/usr/bin
	install -m 0755 ${S}/test/.libs/nm-online ${D}/usr/bin

	install -d ${D}/etc/NetworkManager/

	# Install an empty VPN folder as nm-connection-editor will happily segfault without it :o.
	# With or without VPN support built in ;).
	install -d ${D}/etc/NetworkManager/VPN
}

PACKAGES =+ "libnmutil libnmglib libnmglib-vpn ${PN}-tests" 

FILES_libnmutil += "${libdir}/libnm-util.so.*"
FILES_libnmglib += "${libdir}/libnm_glib.so.*"
FILES_libnmglib-vpn += "${libdir}/libnm_glib_vpn.so.*"


FILES_${PN} += " \
		${libexecdir} \
		${libdir}/pppd/*/nm-pppd-plugin.so \
		${libdir}/NetworkManager/*.so \
		${datadir}/polkit-1 \
		${datadir}/dbus-1 \
		${base_libdir}/udev/* \
        ${base_libdir}/systemd \
"

RRECOMMENDS_${PN} += "iptables"
RCONFLICTS_${PN} = "connman"
RDEPENDS_${PN} = "wpa-supplicant dhcp-client \
           ${@base_contains('COMBINED_FEATURES', '3gmodem', 'ppp', '', d)} \
           "

FILES_${PN}-dbg += "${libdir}/NetworkManager/.debug/ \
		    ${libdir}/pppd/*/.debug/ "

FILES_${PN}-dev += "${datadir}/NetworkManager/gdb-cmd \
                    ${libdir}/pppd/*/*.a \
                    ${libdir}/pppd/*/*.la \
                    ${libdir}/NetworkManager/*.a \
                    ${libdir}/NetworkManager/*.la"

FILES_${PN}-tests = "${bindir}/nm-tool \
                     ${bindir}/libnm_glib_test \
                     ${bindir}/nminfotest \
                     ${bindir}/nm-online \
                     ${bindir}/nm-supplicant \
                     ${bindir}/nm-testdevices"


pkg_postinst_${PN}() {
    # can't do this offline
    if [ "x$D" != "x" ]; then
        exit 1
    fi
    
    systemctl enable NetworkManager.service
}

pkg_prerm_${PN}() {
    # can't do this offline
    if [ "x$D" != "x" ]; then
        exit 1
    fi

    systemctl disable NetworkManager.service
}
