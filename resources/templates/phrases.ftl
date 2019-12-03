<#import "common/bootstrap.ftl" as b>

<@b.page>
    <#if phrases?? && (phrases?size > 0)>
        <table class="table table-striped">
            <thead>
                <tr><th>Emoji</th><th>Row</th></tr>
            </thead>
            <tbody>
                <#list phrases as phrase>
                <tr>
                    <td><h3>${phrase.emoji}</h3></td>
                    <td><h3>${phrase.phrase}</h3></td>
                 </tr>
                </#list>
            </tbody>
        </table
    </#if>
    <div class="panel-body">
    <form method="post" method="/phrases">
    <input type="hidden" name="action" value="add"/>
    Emoji<br>
     <input type="text" name="emoji"/><br>
     Phrase: <br>
      <input type="text" name="phrase"/>
      <input type="submit" value="Submit"/>
      </form>
    </div>

</@b.page>