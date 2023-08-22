

# build

```
./mvnw package
```

# run

### Credentials

Create a local `private-credentials/mend-credentials.json` file containing:
```json
{
  "organizationApiKey": "b249e1f563b9c3e91f6... from Mend / Integrate / Organization / API Key",
  "apiBaseUrlV2":       "https://api-saas-eu... from Mend / Integrate / Organization / API Base URL (v2.0)",
  "userEmail":          "paul.smith@sonarsou... from Mend / (user) / Profile / Identity  / Email",
  "userKey":            "4ef23a19eb1f563b9c2... from Mend / (user) / Profile / User Keys  / User Key"
}
```

### Web server
```shell
./mvnw spring-boot:run
```

Open http://localhost:8080/
