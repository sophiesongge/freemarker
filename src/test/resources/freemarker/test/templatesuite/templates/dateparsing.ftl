<#setting locale="en_US">
<#setting time_zone="GMT">
<#setting datetime_format="G yyyy-MM-dd HH:mm:ss.S Z">
<#setting date_format="G yyyy-MM-dd Z">
<#setting time_format="HH:mm:ss.S Z">

<@assertEquals expected="AD 1998-10-30 15:30:44.512 +0000" actual='AD 1998-10-30 19:30:44.512 +0400'?datetime?string />
<@assertEquals expected="AD 1998-10-29 +0000" actual='AD 1998-10-30 +0400'?date?string />
<@assertEquals expected="15:30:44.512 +0000" actual='19:30:44.512 +0400'?time?string />

<@assertEquals expected="AD 1998-10-30 15:30:44.512 +0000" actual='1998-10-30T19:30:44.512+04:00'?datetime.xs?string />
<@assertEquals expected="AD 1998-10-29 +0000" actual='1998-10-30+04:00'?date.xs?string />
<@assertEquals expected="15:30:44.512 +0000" actual='19:30:44.512+04:00'?time.xs?string />
