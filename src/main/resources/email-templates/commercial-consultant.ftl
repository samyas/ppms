Hi {receiverName},
</br>
I have new consultant available for new JAVA/JEE opportunities.
</br>
Here the resume:
</br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Name : ${person.firstName} ${person.lastName} 
</br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Email: ${person.email}
</br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Job: ${person.job}
</br>

<#if person.yearsExperience??>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Experience: ${person.yearsExperience} years
</br>
</#if>

<#if person.phone??>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Phone: ${person.phone}
</br>
</#if>

<#if person.skills??>
Skills:
</br>
<ul>
<#list person.skills as skill>
	<#if skill.name??> 
		<li>${skill_index + 1}. ${skill.name} <#if skill.years??> (${skill.years} years) </#if> <#if skill.level??> (L-${skill.level}) </#if> </li>
	</#if>
</#list>
</ul>
</#if>


</br>
Cordialement, Met vriendelijke groeten, Sincerely,
</br>
-----------------------------
</br>
${sender.firstName} ${sender.lastName}
</br>
<#if companyName??>
${companyName}
</br>
</#if>
${sender.email}
</br>
<#if sender.phone??>
Mob. ${sender.phone}
</br>
</#if>
<#if sender.skype??>
Skype: ${sender.skype}
</#if>