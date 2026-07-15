{{- define "payments-platform.name" -}}
payments-platform
{{- end -}}

{{- define "payments-platform.fullname" -}}
{{- printf "%s-%s" .Release.Name (include "payments-platform.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
