package com.sdl.dxa.modules.generic;

import com.sdl.dxa.modules.generic.model.*;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GenericModuleInitializer {
	private final ViewModelRegistry viewModelRegistry;
    private SemanticMappingRegistry semanticMappingRegistry;
    
    @Autowired
    public GenericModuleInitializer(ViewModelRegistry viewModelRegistry, SemanticMappingRegistry semanticMappingRegistry) {
        this.viewModelRegistry = viewModelRegistry;
        this.semanticMappingRegistry = semanticMappingRegistry;
    }

    @PostConstruct
    public void registerViewModelEntityClasses() {
        // TODO: Implement this for real, currently this is just a dummy implementation to avoid errors
        viewModelRegistry.registerViewEntityClass("Generic:CustomTeaser", CustomTeaser.class);
        viewModelRegistry.registerViewEntityClass("Generic:CodeBlock", CodeBlock.class);
        viewModelRegistry.registerViewEntityClass("Generic:SiteMap", SitemapItem.class);
        viewModelRegistry.registerViewEntityClass("Generic:ShortArticle", Article.class);
        viewModelRegistry.registerViewEntityClass("Generic:Article", Article.class);
        viewModelRegistry.registerViewEntityClass("Generic:GenericArticle", GenericArticle.class);
        viewModelRegistry.registerViewEntityClass("Generic:MediaFiles", CustomArticle.class);
        viewModelRegistry.registerViewEntityClass("Generic:CustomArticleWithImage", CustomArticle.class);
        viewModelRegistry.registerViewEntityClass("Generic:DummyImages", CustomArticle.class);
        // commented by Sudha for testing viewModelRegistry.registerViewEntityClass("Generic:Breadcrumb", NavigationLinks.class);
        viewModelRegistry.registerViewEntityClass("Generic:Breadcrumb", SitemapItem.class);
        viewModelRegistry.registerViewEntityClass("Generic:TopNavigation", NavigationLinks.class);
        
        viewModelRegistry.registerViewEntityClass("Generic:CommentCarousal", PageTitle.class);
        viewModelRegistry.registerViewEntityClass("Generic:VoteNowLink", PageTitle.class);
        viewModelRegistry.registerViewEntityClass("Generic:PageTitle", PageTitle.class);
        viewModelRegistry.registerViewEntityClass("Generic:DummyPageTitle", PageTitle.class);

        viewModelRegistry.registerViewEntityClass("Generic:Country", Country.class);
    	viewModelRegistry.registerViewEntityClass("Generic:Sector", Sector.class);
    	viewModelRegistry.registerViewEntityClass("Generic:CorporateGroup", CorporateGroup.class);
    	viewModelRegistry.registerViewEntityClass("Generic:Dealer", Dealer.class);
    	viewModelRegistry.registerViewEntityClass("Generic:Brand", Brand.class);
        viewModelRegistry.registerViewEntityClass("Generic:BlogDetail", Blog.class);

        viewModelRegistry.registerViewEntityClass("Generic:TabButton", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:FooterLinks", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:FooterSecondNav", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:FooterSocialLinks", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:FooterSponsorLinks", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:SocialMediaLogin", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:BannerTabs", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:TextWidget", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:Sponsors", LinkList.class);
        
        viewModelRegistry.registerViewEntityClass("Generic:SurveyLoginRegisterForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:SurveyQuestionsForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:SurveyQuestionsServicesList", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:BrandSearchForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:UserProfileForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:CompleteProfileForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:GeneralForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:BlogComments", HTMLForm.class);
        
        viewModelRegistry.registerViewEntityClass("Generic:MobileAppSurveyQuestionsForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:MobileAppShortArticle", Article.class);
        viewModelRegistry.registerViewEntityClass("Generic:MobileAppTabButton", LinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:MobileAppSocialMediaLogin", LinkList.class);
        

        viewModelRegistry.registerViewEntityClass("Generic:BlogOtherArticles", CustomContentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:CountryList", CustomContentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:SurveySectorList", CustomContentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:BlogList", CustomContentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:BlogPromotion", CustomContentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:Promotion", CustomContentList.class);
        
        viewModelRegistry.registerViewEntityClass("Generic:SurveyBrandList", BrandSearchList.class);
        viewModelRegistry.registerViewEntityClass("Generic:ScoreboardBrandList", BrandSearchList.class);
        viewModelRegistry.registerViewEntityClass("Generic:SimilarBrandList", BrandSearchList.class);
        viewModelRegistry.registerViewEntityClass("Generic:SearchBrands", BrandSearchList.class);
        viewModelRegistry.registerViewEntityClass("Generic:WinnersBrandList", BrandSearchList.class);

        viewModelRegistry.registerViewEntityClass("Generic:SurveyLastVisit", SurveyLastVisit.class);
        viewModelRegistry.registerViewEntityClass("Generic:SurveyThanksBrandDetail", SurveyThanksBrandDetail.class);
        viewModelRegistry.registerViewEntityClass("Generic:UserHistory", UserHistory.class);
        viewModelRegistry.registerViewEntityClass("Generic:UserRecentVotes", UserHistory.class);
        viewModelRegistry.registerViewEntityClass("Generic:DialogBox", DialogBox.class);

        
        viewModelRegistry.registerViewEntityClass("Generic:HeaderLogo", ImageLink.class);
    	viewModelRegistry.registerViewEntityClass("Generic:HeaderLinks", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:HeaderBanner", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:CategoryCarousal", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:SocialSharing", TagLinkList.class);
        viewModelRegistry.registerViewEntityClass("Generic:HomeConsumer", Article.class);
        viewModelRegistry.registerViewEntityClass("Generic:PromotionPage", Promotion.class);
        viewModelRegistry.registerViewEntityClass("Generic:CommentListThanks", CommentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:CommentListCarousal", CommentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:CommentListBrandInfo", CommentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:CommentListUserProfile", CommentList.class);

       //viewModelRegistry.registerViewEntityClass("Generic:AccordionContent", GeneralPageContent.class);
        viewModelRegistry.registerViewEntityClass("Generic:AccordionContent", CustomItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:AccordionLearn", CustomItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:FBSplashScreen", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:ScoreBoardPromo", CustomItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:SHPromotions", CustomItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:YoutubePopup", CustomItemList.class);



        viewModelRegistry.registerViewEntityClass("Generic:DashboardHeaderLogo", ImageLink.class);
        viewModelRegistry.registerViewEntityClass("Generic:DashboardTopNav", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:DashboardRightLinks", ItemList.class);
        viewModelRegistry.registerViewEntityClass("Generic:DashboardLoginForm", HTMLForm.class);
        viewModelRegistry.registerViewEntityClass("Generic:DashboardCommentListBrandInfo", CommentList.class);
        viewModelRegistry.registerViewEntityClass("Generic:DashboardBrandList", BrandSearchList.class);

        viewModelRegistry.registerViewEntityClass("Generic:Graph", BrandSentimentGraph.class);

        viewModelRegistry.registerViewEntityClass("Generic:CampaignRedirect", CampaignRedirect.class);



        semanticMappingRegistry.registerEntity(ItemList.class);
        semanticMappingRegistry.registerEntity(CustomItemList.class);
        semanticMappingRegistry.registerEntity(LinkList.class);
        semanticMappingRegistry.registerEntity(KeyValuePair.class);
        semanticMappingRegistry.registerEntity(CustomTeaser.class);
        semanticMappingRegistry.registerEntity(CustomArticle.class);
        semanticMappingRegistry.registerEntity(GenericArticle.class);
        semanticMappingRegistry.registerEntity(CustomContentList.class);
        semanticMappingRegistry.registerEntity(PageTitle.class);
        semanticMappingRegistry.registerEntity(CodeBlock.class);
        semanticMappingRegistry.registerEntity(QueryFilter.class);
        semanticMappingRegistry.registerEntity(HTMLForm.class);
        semanticMappingRegistry.registerEntity(HTMLFormElement.class);
        semanticMappingRegistry.registerEntity(CountryRelatedFields.class);
        semanticMappingRegistry.registerEntity(CountryRelatedBrandFields.class);
        semanticMappingRegistry.registerEntity(LogoImage.class);
        semanticMappingRegistry.registerEntity(AwardImage.class);
        semanticMappingRegistry.registerEntity(SvgImageCombo.class);
        semanticMappingRegistry.registerEntity(Country.class);
        semanticMappingRegistry.registerEntity(Sector.class);
        semanticMappingRegistry.registerEntity(CorporateGroup.class);
        semanticMappingRegistry.registerEntity(Dealer.class);
        semanticMappingRegistry.registerEntity(Brand.class);
        semanticMappingRegistry.registerEntity(BrandAwards.class);
        semanticMappingRegistry.registerEntity(MapLocation.class);
        semanticMappingRegistry.registerEntity(ContactInforamtion.class);
        semanticMappingRegistry.registerEntity(Blog.class);
        semanticMappingRegistry.registerEntity(UserHistory.class);
        semanticMappingRegistry.registerEntity(VoteHistory.class);
        semanticMappingRegistry.registerEntity(SurveyBrandList.class);
        semanticMappingRegistry.registerEntity(BrandList.class);
        semanticMappingRegistry.registerEntity(SurveyLastVisit.class);
        semanticMappingRegistry.registerEntity(SurveyThanksBrandDetail.class);
        semanticMappingRegistry.registerEntity(Promotion.class);
        semanticMappingRegistry.registerEntity(CommentList.class);
        semanticMappingRegistry.registerEntity(UserComment.class);
        semanticMappingRegistry.registerEntity(BrandSearchList.class);
        semanticMappingRegistry.registerEntity(BrandSearch.class);
        semanticMappingRegistry.registerEntity(DialogBox.class);
        semanticMappingRegistry.registerEntity(ContentParagraph.class);
        semanticMappingRegistry.registerEntity(ContentSubParagraph.class);
        semanticMappingRegistry.registerEntity(ContentSubSecondParagraph.class);
        semanticMappingRegistry.registerEntity(GeneralPageContent.class);
        semanticMappingRegistry.registerEntity(CampaignRedirect.class);
        semanticMappingRegistry.registerEntity(BrandSentimentGraph.class);
        semanticMappingRegistry.registerEntity(SentimentGraphList.class);


        semanticMappingRegistry.registerEntity(Paragraph.class);
        semanticMappingRegistry.registerEntity(MediaItem.class);
        semanticMappingRegistry.registerEntity(ContentList.class);
        semanticMappingRegistry.registerEntity(ImageLink.class);
        semanticMappingRegistry.registerEntity(NavigationLinks.class);
        semanticMappingRegistry.registerEntity(SitemapItem.class);
        semanticMappingRegistry.registerEntity(Image.class);
 
    }    
}
