/**
 * Your Copyright Here
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */
#import "TiModule.h"

@interface AppersonlabsCarbonModule : TiModule {
@private
	NSMutableDictionary *tibs;
}
@property (nonatomic, strong) TiModule * uimodule;
@property (nonatomic, strong) NSMutableArray * stylesheets;
@end
