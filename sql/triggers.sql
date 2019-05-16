CREATE TRIGGER clearActivationTokensTrigger
     AFTER INSERT ON activationTokens
     FOR EACH STATEMENT
     EXECUTE PROCEDURE cleanActivationTokens();