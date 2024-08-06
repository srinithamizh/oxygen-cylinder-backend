
# Oxygen Cylinder 📈₹📉

*Oxygen Cylinder* is a tool to monitor your money. It is your essential companion for managing finances with ease. Just as *oxygen* fuels life, This tool provides financial clarity by tracking your expenses, setting budgets, and offering insightful reports. Stay on top of your spending and breathe easy, knowing your finances are in good hands.

### Run the application using Docker
1. Build the Spring boot application using maven.
    ```bash
    .\mvnw clean install
    ```
2. Once it is done, Build the Spring boot application using Dockerfile.
    ```bash
    docker build -t oxygen-cylinder .
    ```
3. Run the built docker image.
    ```bash
    docker run -p 8080:8080 oxygen-cylinder
    ```
4. Hit below url in the browser
    ```bash
    http://localhost:8080
    ```
## Author

- [Thamizharasan Srinivasan](https://www.linkedin.com/in/srinithamizh/)


## Feedback

If you have any feedback, please reach out to us at srinithamizh@gmail.com

